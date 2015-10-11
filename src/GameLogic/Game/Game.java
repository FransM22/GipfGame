package GameLogic.Game;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

import static GameLogic.Direction.*;
import static GameLogic.Piece.*;
import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;
import static java.util.stream.Collectors.toList;
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
    public PlayersInGame players;
    GipfBoardState gipfBoardState;                              // The board where the pieces are stored.
    private Set<Position> currentRemoveSelection = new HashSet<>();

    Game(GameType gameType) {
        this.gameType = gameType;

        initializePlayers();
        initializeBoard();

        boardHistory = new ArrayList<>();
        boardHistory.add(gipfBoardState);

        gameLogger = new GameLogger(gameType);
    }

    void initializePlayers() {
        players = new PlayersInGame();
        players.setStartingPlayer(players.get(WHITE));
    }

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
        if (players.winner() != null) return;

        GipfBoardState newGipfBoardState = new GipfBoardState(gipfBoardState);  // If the move succeeds, newGipfBoardState will be the new gipfBoardState

        if (players.current().reserve >= move.addedPiece.getPieceValue()) {
            setPiece(newGipfBoardState, move.startPos, move.addedPiece);   // Add the piece to the board on the starting position

            try {
                movePiecesTowards(newGipfBoardState, move.startPos, move.direction);

                if (move.addedPiece.getPieceType() == PieceType.GIPF) {
                    players.current().hasPlacedGipfPieces = true;
                }

                gipfBoardState.whiteIsOnTurn = players.current() == players.get(WHITE);
                gipfBoardState.whitePiecesLeft = players.get(WHITE).reserve;
                gipfBoardState.blackPiecesLeft = players.get(BLACK).reserve;
                gipfBoardState.blackHasPlacedNormalPieces = players.get(WHITE).hasPlacedNormalPieces;
                gipfBoardState.whiteHasPlacedNormalPieces = players.get(BLACK).hasPlacedNormalPieces;

                boardHistory.add(gipfBoardState);
                gipfBoardState = newGipfBoardState;

                HashMap<PieceColor, Set<LineSegment>> linesTakenBy = new HashMap<>();
                linesTakenBy.put(WHITE, new HashSet<>());
                linesTakenBy.put(BLACK, new HashSet<>());

                HashMap<PieceColor, Set<Position>> piecesBackTo = new HashMap<>();
                piecesBackTo.put(WHITE, new HashSet<>());
                piecesBackTo.put(BLACK, new HashSet<>());
                piecesBackTo.put(null, new HashSet<>());    // Hash maps can take null as a key, in this case used for pieces that are removed from the board

                // Get the lines of the own color
                removeLines(newGipfBoardState, players.current().pieceColor, linesTakenBy, piecesBackTo);

                // Get lines of the opponent
                PieceColor opponentColor = players.current().pieceColor == WHITE ? BLACK : WHITE;
                removeLines(newGipfBoardState, opponentColor, linesTakenBy, piecesBackTo);

                // Get the line segments that
                // Get the lines of the color of the other player

                gameLogger.log(move.toString());
                piecesBackTo.entrySet().forEach(e -> removePiecesFromBoard(newGipfBoardState, e.getValue()));

                for (PieceColor pieceColor : PieceColor.values()) {
                    if (piecesBackTo.get(pieceColor).size() != 0) {
                        players.get(pieceColor).reserve += piecesBackTo.get(pieceColor).size();

                        gameLogger.log(pieceColor + " retrieved " + piecesBackTo.get(pieceColor).size() + " pieces");
                    }
                }

                // Update for the last added piece
                players.current().reserve -= move.addedPiece.getPieceValue();

                if (getGameOverState()) {
                    players.updateCurrent();
                    players.makeCurrentPlayerWinner();

                    gameLogger.log("Game over! " + players.winner().pieceColor + " won!");
                } else {
                    players.updateCurrent();
                }

                if (!players.current().isPlacingGipfPieces) {
                    players.current().hasPlacedNormalPieces = true;
                }

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
        if (players.winner() != null) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(
                new Move(getCurrentPiece(), new Position('a', 1), NORTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 2), NORTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 2), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 3), NORTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 3), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 4), NORTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 4), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('a', 5), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('b', 6), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('b', 6), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('c', 7), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('c', 7), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('d', 8), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('d', 8), SOUTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('e', 9), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('f', 8), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('f', 8), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('g', 7), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('g', 7), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('h', 6), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('h', 6), SOUTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 5), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 4), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 4), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 3), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 3), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 2), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 2), SOUTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('i', 1), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('h', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('h', 1), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('g', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('g', 1), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('f', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('f', 1), NORTH_WEST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('e', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('d', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('d', 1), NORTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('c', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('c', 1), NORTH_EAST, Optional.empty()),
                new Move(getCurrentPiece(), new Position('b', 1), NORTH, Optional.empty()),
                new Move(getCurrentPiece(), new Position('b', 1), NORTH_EAST, Optional.empty())
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
                new Line(this, new Position('a', 1), SOUTH_EAST).getPositions().stream(),
                Stream.concat(new Line(this, new Position('e', 1), NORTH_EAST).getPositions().stream(),
                        Stream.concat(new Line(this, new Position('i', 1), NORTH).getPositions().stream(),
                                Stream.concat(new Line(this, new Position('i', 5), NORTH_WEST).getPositions().stream(),
                                        Stream.concat(
                                                new Line(this, new Position('e', 9), SOUTH_WEST).getPositions().stream(),
                                                new Line(this, new Position('a', 5), SOUTH).getPositions().stream()
                                        )
                                )
                        )
                )
        ).collect(toSet());
    }

    public Piece getCurrentPiece() {
        PlayersInGame.Player currentPlayer = players.current();
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

    /**
     * By Dingding
     */
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
        if (boardHistory.size() > 1 && !getGameOverState()) {
            gipfBoardState = boardHistory.get(boardHistory.size() - 1);
            players.setCurrent(gipfBoardState.whiteIsOnTurn ? players.get(WHITE) : players.get(BLACK));
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

    public void removePiecesFromBoard(GipfBoardState gipfBoardState, Set<Position> positions) {
        for (Position position : positions) {
            gipfBoardState.getPieceMap().remove(position);
        }
    }

    private void removeLines(GipfBoardState gipfBoardState, PieceColor pieceColor, Map<PieceColor, Set<LineSegment>> linesTakenBy, Map<PieceColor, Set<Position>> piecesBackTo) {
        Set<LineSegment> intersectingSegments;
        Set<LineSegment> segmentsNotRemoved = new HashSet<>();

        do {
            intersectingSegments = new HashSet<>();
            Set<LineSegment> removableLineSegmentsThisPlayer = getRemovableLineSegments(gipfBoardState, pieceColor);
            for (LineSegment segment : removableLineSegmentsThisPlayer) {
                // Remove the line segments that are not intersecting with other line segments of the set
                boolean intersectionFound = false;

                for (LineSegment otherSegment : removableLineSegmentsThisPlayer) {
                    if (!segment.equals(otherSegment) && !segmentsNotRemoved.contains(otherSegment)) {
                        if (segment.intersectsWith(otherSegment)) {
                            if (!segmentsNotRemoved.contains(segment)) {
                                intersectingSegments.add(segment);
                                intersectionFound = true;
                            }
                        }
                    }
                }

                if (!intersectionFound) {
                    if (!segmentsNotRemoved.contains(segment)) {
                        linesTakenBy.get(pieceColor).add(segment);
                    }
                }
            }

            if (intersectingSegments.size() > 0) {
                LineSegment lineSegment = intersectingSegments.iterator().next();
                currentRemoveSelection = lineSegment.getOccupiedPositions(gipfBoardState);
                int dialogResult = GipfBoardComponent.showConfirmDialog(players.current().pieceColor + ", do you want to remove " + lineSegment.getOccupiedPositions(gipfBoardState).stream().map(Position::getName).sorted().collect(toList()) + "?", "Remove line segment");
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // Remove the line
                    linesTakenBy.get(pieceColor).add(lineSegment);
                } else if (dialogResult == JOptionPane.NO_OPTION) {
                    // Don't remove the line
                    segmentsNotRemoved.add(lineSegment);
                }
                currentRemoveSelection = new HashSet<>();
            }

            for (LineSegment segment : linesTakenBy.get(pieceColor)) {
                segment.getOccupiedPositions(gipfBoardState).forEach(position ->
                        {
                            Piece piece = gipfBoardState.getPieceMap().get(position);
                            if (piece.getPieceColor() == pieceColor) {
                                if (piece.getPieceType() == PieceType.NORMAL || doesPlayerWantToRemoveGipf(position)) {
                                    piecesBackTo.get(pieceColor).add(position);
                                }
                            } else {
                                if (piece.getPieceType() == PieceType.NORMAL || doesPlayerWantToRemoveGipf(position)) {
                                    piecesBackTo.get(null).add(position);
                                }
                            }
                        }
                );
            }

            piecesBackTo.entrySet()
                    .forEach(e -> removePiecesFromBoard(gipfBoardState, e.getValue()));
        }
        while (intersectingSegments.size() > 0);
    }

    public Set<Position> getCurrentRemoveSelection() {
        return currentRemoveSelection;
    }

    /**
     * Cannot be called if winningPlayer is not null. Determines whether there is a winning player at this moment in the
     * game, and if so, set the winningPlayer pointer accordingly.
     * @return true if the game over condition has been fulfilled, false otherwise.
     */
    public abstract boolean getGameOverState();

    public boolean doesPlayerWantToRemoveGipf(Position position) {
        currentRemoveSelection.add(position);
        int dialogResult = GipfBoardComponent.showConfirmDialog(players.current().pieceColor + ", do you want to remove the Gipf at " + position.getName() + "?", "Remove Gipf");
        currentRemoveSelection = new HashSet<>();
        return dialogResult == JOptionPane.YES_OPTION;
    }
}
