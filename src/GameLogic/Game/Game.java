package GameLogic.Game;

import GameLogic.*;

import java.util.*;
import java.util.stream.Stream;

import static GameLogic.Piece.*;
import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;
import static java.util.stream.Collectors.toSet;

/**
 * The Game class controls whether the moves are according to the game rules, and if so, applies those moves to the board
 * <p/>
 * Created by frans on 21-9-2015.
 */
public abstract class Game {
    private final GameLogger gameLogger;
    private final List<GipfBoardState> boardHistory;            // Stores the history of the boards
    private final GameType gameType;                            // The game type (basic, standard, tournament)
    public Map<PieceColor, Player> players = new HashMap<>();
    //    public Player whitePlayer = null;                           // The black and white player
//    public Player blackPlayer = null;
    public boolean isGameOver = false;                          // Is only true if the game is finished
    GipfBoardState gipfBoardState;                              // The board where the pieces are stored.
    private Player currentPlayer;                               // Acts as a pointer to the current player
    private Player winningPlayer;                               // Acts as a pointer to the winning player

    Game(GameType gameType) {
        this.gameType = gameType;

        initializePlayers();
        initializeBoard();

        boardHistory = new ArrayList<>();
        boardHistory.add(gipfBoardState);

        currentPlayer = players.get(WHITE);

        gameLogger = new GameLogger(gameType);
    }

    abstract void initializePlayers();

    void initializeBoard() {
        this.gipfBoardState = new GipfBoardState();
    }

    /**
     * Checks whether the position is located on the whole board. Either on the inner area or on the outer positions
     * where pieces can start a move, but never end on it.
     *
     * @param p the position of which should be determined whether it is on the bigger board
     */
    public boolean isPositionOnBigBoard(Position p) {
        int col = p.getColName() - 'a' + 1;
        int row = p.getRowNumber();

        // See google doc for explanation of the formula
        return !(row <= 0 ||
                col >= 10 ||
                row + col >= 15 ||
                col - row <= -5 ||
                col <= 0
        );
    }

    /**
     * Checks whether the position is located on the inner board. Returns false for positions on the outer positions, as well
     * as positions that are not on the board.
     * <p/>
     * By Leroy
     *
     * @param p position of which is to be determined whether the position is located on the inner board
     */
    private boolean isOnInnerBoard(Position p) {
        int col = p.getColName() - 'a' + 1;
        int row = p.getRowNumber();

        // See google doc for explanation of the formula
        return !(row <= 1 ||
                col >= 9 ||
                row + col >= 14 ||
                col - row <= -4 ||
                col <= 1
        );
    }

    private boolean isPositionEmpty(GipfBoardState gipfBoardState, Position p) {
        return !gipfBoardState.getPieceMap().containsKey(p);
    }

    private void movePiece(GipfBoardState gipfBoardState, Position currentPosition, int deltaPos) throws Exception {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isOnInnerBoard(nextPosition)) {
            throw new InvalidMoveException();
        } else {
            try {
                if (!isPositionEmpty(gipfBoardState, nextPosition)) {
                    movePiece(gipfBoardState, nextPosition, deltaPos);
                }

                gipfBoardState.getPieceMap().put(nextPosition, gipfBoardState.getPieceMap().remove(currentPosition));
            } catch (InvalidMoveException e) {
                gameLogger.log("Moving to " + nextPosition.getName() + " is not allowed");
                throw new InvalidMoveException();
            }
        }
    }

    private void movePiecesTowards(GipfBoardState gipfBoardState, Position startPos, Direction direction) throws InvalidMoveException {
        int deltaPos = direction.getDeltaPos();

        Position currentPosition = new Position(startPos);

        try {
            movePiece(gipfBoardState, currentPosition, deltaPos);
        } catch (Exception e) {
            throw new InvalidMoveException();
        }
    }

    /**
     * applyMove applies the given move to the board.
     * First, the new piece is added to the startPos
     * Then the pieces are moved in the direction of the move,
     * and finally pieces that need to be removed are removed from the board
     *
     * @param move the move that is applied
     */
    public void applyMove(Move move) {
        if (isGameOver) return;

        GipfBoardState newGipfBoardState = new GipfBoardState(gipfBoardState);  // If the move succeeds, newGipfBoardState will be the new gipfBoardState

        if (currentPlayer.reserve >= move.addedPiece.getPieceValue()) {
            setPiece(newGipfBoardState, move.startPos, move.addedPiece);   // Add the piece to the board on the starting position

            try {
                movePiecesTowards(newGipfBoardState, move.startPos, move.direction);
                Set<LineSegment> intersectingSegments = new HashSet<>();

                HashMap<PieceColor, Set<Position>> piecesBackTo = new HashMap<>();
                piecesBackTo.put(WHITE, new HashSet<>());
                piecesBackTo.put(BLACK, new HashSet<>());

                HashMap<PieceColor, Set<LineSegment>> linesTakenBy = new HashMap<>();
                linesTakenBy.put(WHITE, new HashSet<>());
                linesTakenBy.put(BLACK, new HashSet<>());

                Set<Position> piecesDestroyed = new HashSet<>();


                // Get the lines of the own color
                Set<LineSegment> removableLineSegmentsCurrentPlayer = getRemovableLineSegments(newGipfBoardState, currentPlayer.pieceColor);
                for (LineSegment segment : removableLineSegmentsCurrentPlayer) {
                    // Remove the line segments that are not intersecting with other line segments of the set
                    boolean intersectionFound = false;

                    for (LineSegment otherSegment : removableLineSegmentsCurrentPlayer) {
                        if (!segment.equals(otherSegment)) {
                            if (segment.intersectsWith(otherSegment)) {
                                intersectingSegments.add(segment);
                                intersectingSegments.add(otherSegment);

                                intersectionFound = true;
                            }
                        }
                    }

                    if (!intersectionFound) {
                        linesTakenBy.get(currentPlayer.pieceColor).add(segment);
                     }
                }

                if (intersectingSegments.size() > 0) {
                    // TODO fix this
                    gameLogger.log("Not removing intersecting line segments (not implemented): ");
                    intersectingSegments.stream().forEach(e -> gameLogger.log(e.toString()));
                }

                for (LineSegment segment : linesTakenBy.get(currentPlayer.pieceColor)) {
                    segment.getOccupiedPositions(newGipfBoardState).forEach(position ->
                            {
                                if (newGipfBoardState.getPieceMap().get(position).getPieceColor() == currentPlayer.pieceColor) {
                                    piecesBackTo.get(currentPlayer.pieceColor).add(position);
                                } else {
                                    piecesDestroyed.add(position);
                                }
                            }
                    );
                }

                removePiecesFromBoard(newGipfBoardState, piecesDestroyed);
                for (PieceColor pieceColor : PieceColor.values()) {
                    removePiecesFromBoard(newGipfBoardState, piecesBackTo.get(pieceColor));
                }

                // Get lines of the opponent
                PieceColor opponentColor = currentPlayer.pieceColor == WHITE ? BLACK : WHITE;
                Set<LineSegment> removableLineSegmentsOpponent = getRemovableLineSegments(newGipfBoardState, opponentColor);
                for (LineSegment segment : removableLineSegmentsOpponent) {
                    // Remove the line segments that are not intersecting with other line segments of the set
                    boolean intersectionFound = false;

                    for (LineSegment otherSegment : removableLineSegmentsOpponent) {
                        if (!segment.equals(otherSegment)) {
                            if (segment.intersectsWith(otherSegment)) {
                                intersectingSegments.add(segment);
                                intersectingSegments.add(otherSegment);

                                intersectionFound = true;
                            }
                        }
                    }

                    if (!intersectionFound) {
                        linesTakenBy.get(opponentColor).add(segment);
                    }
                    gameLogger.log("Not removing intersecting line segments: ");
                    intersectingSegments.stream().forEach(e -> gameLogger.log(e.toString()));
                }

                for (LineSegment segment : linesTakenBy.get(opponentColor)) {
                    segment.getOccupiedPositions(newGipfBoardState).forEach(position ->
                            {
                                if (newGipfBoardState.getPieceMap().get(position).getPieceColor() == opponentColor) {
                                    piecesBackTo.get(opponentColor).add(position);
                                } else {
                                    piecesDestroyed.add(position);
                                }
                            }
                    );
                }

                // Get the line segments that
                // Get the lines of the color of the other player

//                for (Map.Entry<Line, PieceColor> entry : removableLines.entrySet()) {
//                    Line line = entry.getKey();
//                    PieceColor rowColor = entry.getValue();
//
//                    for (Map.Entry<Line, PieceColor> entryOherLine : removableLines.entrySet()) {
//                        Line otherLine = entryOherLine.getKey();
//                        PieceColor otherRowColor = entryOherLine.getValue();
//
//                        if (line.intersectsWith(otherLine) && !line.equals(otherLine)) {
//                            getGameLogger().log("Intersection of 2 lines found ( " + line + " intersects with " + otherLine);
//                            if (rowColor.equals(otherRowColor)) {
//                                getGameLogger().log("  RowColor == otherRowColor");
//                                // TODO The current player has to choose which row to remove
//                            } else if (!rowColor.equals(otherRowColor)) {
//                                getGameLogger().log("  RowColor != otherRowColor");
//                                if (rowColor.equals(currentPlayer.pieceColor)) {
//                                    if (rowColor.equals(WHITE)) {
//                                        linesTakenByWhite.add(line);
//                                        linesNotRemoved.add(otherLine);
//                                    } else if (rowColor.equals(BLACK)) {
//                                        linesTakenByBlack.add(line);
//                                        linesNotRemoved.add(otherLine);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if (!(linesTakenByBlack.contains(line) || linesTakenByWhite.contains(line) || linesNotRemoved.contains(line))) {
//                        getGameLogger().log("No intersection found for " + line);
//                        getGameLogger().log("  White lines: " + linesTakenByWhite);
//                        getGameLogger().log("  Black lines: " + linesTakenByBlack);
//                        getGameLogger().log("  Not removed: " + linesNotRemoved);
//                        // If there is no intersection found, add the row to the color of the four pieces set
//                        if (rowColor.equals(WHITE)) {
//                            linesTakenByWhite.add(line);
//                        } else if (rowColor == BLACK) {
//                            linesTakenByBlack.add(line);
//                        }
//                    }
//                }
//
//                // Determine the pieces that are going back to the white player
//                for (Line line : linesTakenByWhite) {
//                    for (Position position : line.getPositions()) {
//                        if (newGipfBoardState.getPieceMap().containsKey(position)) {
//                            if (newGipfBoardState.getPieceMap().get(position).getPieceColor() == WHITE) {
//                                piecesBackToWhite.add(position);
//                            } else {
//                                piecesRemoved.add(position);
//                            }
//                        }
//                    }
//                }
//
//                // Determine the pieces that are going back to the black player
//                for (Line line : linesTakenByBlack) {
//                    for (Position position : line.getPositions()) {
//                        if (newGipfBoardState.getPieceMap().containsKey(position)) {
//                            if (newGipfBoardState.getPieceMap().get(position).getPieceColor() == BLACK) {
//                                piecesBackToBlack.add(position);
//                            } else {
//                                piecesRemoved.add(position);
//                            }
//                        }
//                    }
//                }
//
//                // Remove the pieces
//                move.removedPiecePositions = Stream.concat(Stream.concat(piecesBackToWhite.stream(), piecesBackToBlack.stream()), piecesRemoved.stream()).collect(toSet());
//                move.removedPiecePositions.stream().forEach(newGipfBoardState.getPieceMap()::remove);
//
                gipfBoardState.whiteIsOnTurn = currentPlayer == players.get(WHITE);
                gipfBoardState.whitePiecesLeft = players.get(WHITE).reserve;
                gipfBoardState.blackPiecesLeft = players.get(BLACK).reserve;
                gipfBoardState.blackHasPlacedNormalPieces = players.get(WHITE).hasPlacedNormalPieces;
                gipfBoardState.whiteHasPlacedNormalPieces = players.get(BLACK).hasPlacedNormalPieces;

                gameLogger.log(move.toString());
                for (PieceColor pieceColor : PieceColor.values()) {
                    if (piecesBackTo.get(pieceColor).size() != 0) {
                        players.get(pieceColor).reserve += piecesBackTo.get(pieceColor).size();

                        gameLogger.log(pieceColor + " retrieved " + piecesBackTo.get(pieceColor).size() + " pieces");
                    }
                }

                // Update for the last added piece
                currentPlayer.reserve -= move.addedPiece.getPieceValue();

                if (currentPlayer.reserve == 0) {
                    updateCurrentPlayer();
                    isGameOver = true;
                    winningPlayer = currentPlayer;

                    gameLogger.log("Game over! " + winningPlayer.pieceColor + " won!");
                } else {
                    updateCurrentPlayer();
                }

                if (!currentPlayer.isPlacingGipfPieces) {
                    currentPlayer.hasPlacedNormalPieces = true;
                }

                boardHistory.add(gipfBoardState);
                gipfBoardState = newGipfBoardState;

            } catch (InvalidMoveException e) {
                System.out.println("Move not applied");
            }
        } else {
            gameLogger.log("No pieces left");
        }
    }

    public void setPiece(GipfBoardState gipfBoardState, Position pos, Piece piece) {
        gipfBoardState.getPieceMap().put(pos, piece);
    }

    public GipfBoardState getGipfBoardState() {
        return gipfBoardState;
    }

    /**
     * This method is currently a placeholder. Currently statically returns all potential candidates for allowed moves,
     * but it should be checked which ones are actually allowed.
     */
    private Set<Move> getAllowedMoves() {
        if (isGameOver) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(
                new Move(getCurrentPiece(), new Position('a', 1), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 2), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 2), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 3), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 3), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 4), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 4), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 5), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('b', 6), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('b', 6), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('c', 7), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('c', 7), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('d', 8), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('d', 8), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('e', 9), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('f', 8), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('f', 8), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('g', 7), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('g', 7), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('h', 6), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('h', 6), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('i', 5), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 4), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 4), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 3), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 3), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 2), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 2), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('h', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('h', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('g', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('g', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('f', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('f', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('e', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('d', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('d', 1), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('c', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('c', 1), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('b', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('b', 1), Direction.NORTH_EAST)
        ));
    }

    /**
     * Gets the border positions (dots) by concatenating all the positions per border line. The border lines are hardcoded in this
     * method.
     *
     * @return a set of positions positioned just out of the play area
     */
    private Set<Position> getDots() {
        return Stream.concat(
                new Line(this, new Position('a', 1), Direction.SOUTH_EAST).getPositions().stream(),
                Stream.concat(new Line(this, new Position('e', 1), Direction.NORTH_EAST).getPositions().stream(),
                        Stream.concat(new Line(this, new Position('i', 1), Direction.NORTH).getPositions().stream(),
                                Stream.concat(new Line(this, new Position('i', 5), Direction.NORTH_WEST).getPositions().stream(),
                                        Stream.concat(
                                                new Line(this, new Position('e', 9), Direction.SOUTH_WEST).getPositions().stream(),
                                                new Line(this, new Position('a', 5), Direction.SOUTH).getPositions().stream()
                                        )
                                )
                        )
                )
        ).collect(toSet());
    }

    private void updateCurrentPlayer() {
        currentPlayer = ((currentPlayer == players.get(WHITE)) ? players.get(BLACK) : players.get(WHITE));
    }

    public Piece getCurrentPiece() {
        if (currentPlayer.pieceColor == WHITE && currentPlayer.isPlacingGipfPieces)
            return WHITE_GIPF;
        else if (currentPlayer.pieceColor == WHITE)
            return WHITE_SINGLE;
        else if (currentPlayer.pieceColor == BLACK && currentPlayer.isPlacingGipfPieces)
            return BLACK_GIPF;
        else if (currentPlayer.pieceColor == BLACK)
            return BLACK_SINGLE;

        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * By Dingding
     */
    private Map<Line, PieceColor> detectFourPieces(GipfBoardState gipfBoardState) {
        Map<Line, PieceColor> removableLines = new HashMap<>();

        Set<Line> linesOnTheBoard = Line.getLinesOnTheBoard(this);

        for (Line line : linesOnTheBoard) {
            Position currentPosition = line.getStartPosition();
            Direction direction = line.getDirection();

            int consecutivePieces = 0;
            PieceColor consecutivePiecesColor = null;

            for (; isPositionOnBigBoard(currentPosition); currentPosition = new Position(currentPosition.getPosId() + direction.getDeltaPos())) {
                PieceColor currentPieceColor = null;

                if (gipfBoardState.getPieceMap().containsKey(currentPosition)) {
                    currentPieceColor = gipfBoardState.getPieceMap().get(currentPosition).getPieceColor();
                }

                if (currentPieceColor != consecutivePiecesColor) {
                    if (consecutivePiecesColor != null && consecutivePieces >= 4) {
                        removableLines.put(new Line(this, currentPosition, direction), consecutivePiecesColor);
                        break;
                    }

                    consecutivePiecesColor = currentPieceColor;
                    consecutivePieces = 1;
                } else {
                    consecutivePieces++;
                }
            }
        }

        return removableLines;
    }

    private Set<LineSegment> getRemovableLineSegments(GipfBoardState gipfBoardState, PieceColor pieceColor) {
        Set<LineSegment> removableLines = new HashSet<>();
        Set<Line> linesOnTheBoard = Line.getLinesOnTheBoard(this);

        for (Line line : linesOnTheBoard) {
            Position currentPosition = line.getStartPosition();
            Position startOfSegment = null;
            Position endOfSegment = null;
            Direction direction = line.getDirection();
            int consecutivePieces = 0;
            boolean isInLineSegment = false;

            // Break the for-loop if an endOfSegment has been found (because the largest lines only have 7 positions on the board, there
            // can't be more than one set of four pieces of the same color (requiring at least 9 positions) on the board.
            for (; isPositionOnBigBoard(currentPosition) && endOfSegment == null; currentPosition = currentPosition.next(direction)) {
                PieceColor currentPieceColor = gipfBoardState.getPieceMap().containsKey(currentPosition) ? gipfBoardState.getPieceMap().get(currentPosition).getPieceColor() : null;

                // Update the consecutivePieces
                if (currentPieceColor == pieceColor) {
                    consecutivePieces++;
                }
                if (consecutivePieces >= 4) {
                    isInLineSegment = true;
                }
                if (currentPieceColor != pieceColor) {
                    consecutivePieces = 0;
                }

                if (isInLineSegment) {
                    if (getDots().contains(currentPosition) || currentPieceColor == null) {
                        endOfSegment = currentPosition.previous(direction);
                    }
                }


                // Update the startOfSegment if necessary
                if (startOfSegment == null) {
                    if (currentPieceColor != null) {
                        startOfSegment = currentPosition;
                    }
                }
                if (currentPieceColor == null && endOfSegment == null) {
                    startOfSegment = null;
                }

                // Add a line segment to the list if we have found one
                if (endOfSegment != null) {
                    removableLines.add(new LineSegment(this, startOfSegment, endOfSegment, direction));
                }
            }
        }

        return removableLines;
    }

    public Set<Position> getStartPositionsForMoves() {
        return getAllowedMoves()
                .stream()
                .map(Move::getStartingPosition)
                .collect(toSet());
    }

    public Set<Position> getMoveToPositionsForStartPosition(Position position) {
        return getAllowedMoves()
                .stream()
                .filter(m -> m.getStartingPosition().equals(position))
                .map(move -> new Position(
                        move.getStartingPosition().getPosId() + move.getDirection().getDeltaPos()))
                .collect(toSet());
    }

    public void returnToPreviousBoard() {
        if (boardHistory.size() > 1 && !isGameOver) {
            gipfBoardState = boardHistory.get(boardHistory.size() - 1);
            currentPlayer = gipfBoardState.whiteIsOnTurn ? players.get(WHITE) : players.get(BLACK);
            players.get(WHITE).reserve = gipfBoardState.whitePiecesLeft;
            players.get(WHITE).hasPlacedNormalPieces = gipfBoardState.whiteHasPlacedNormalPieces;
            players.get(BLACK).reserve = gipfBoardState.blackPiecesLeft;
            players.get(BLACK).hasPlacedNormalPieces = gipfBoardState.blackHasPlacedNormalPieces;

            boardHistory.remove(boardHistory.size() - 1);

            gameLogger.log("Returned to previous game state");
        }
    }

    public GameLogger getGameLogger() {
        return gameLogger;
    }

    public GameType getGameType() {
        return gameType;
    }

    public Player getWinningPlayer() {
        return winningPlayer;
    }

    public void removePiecesFromBoard(GipfBoardState gipfBoardState, Set<Position> positions) {
        for (Position position : positions) {
            gipfBoardState.getPieceMap().remove(position);
        }
    }
}
