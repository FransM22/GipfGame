package GameLogic.Game;

import AI.Players.HumanPlayer;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.*;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static GameLogic.Direction.*;
import static GameLogic.Piece.*;
import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;
import static GameLogic.PieceType.GIPF;
import static GameLogic.PieceType.NORMAL;
import static java.util.stream.Collectors.*;

/**
 * The Game class controls whether the moves are according to the game rules, and if so, applies those moves to the board
 * <p/>
 * Created by frans on 21-9-2015.
 */
public abstract class Game implements Serializable {
    private final BoardHistory boardHistory;            // Stores the history of the boards
    GipfBoardState gipfBoardState;                              // The board where the pieces are stored.
    private GameLogger gameLogger;
    private Function<GipfBoardState, Move> whitePlayer;
    private Function<GipfBoardState, Move> blackPlayer;
    private Set<Position> currentRemoveSelection = new HashSet<>(); // Makes it possible for the gipfboardcomponent to display crosses on the pieces and lines that can be selected for removal
    private Thread automaticPlayThread;

    Game() {
        initializeBoard();
        initializePlayers();

        whitePlayer = new HumanPlayer();
        blackPlayer = new HumanPlayer();

        boardHistory = new BoardHistory();
        boardHistory.add(gipfBoardState);

        gameLogger = new GameLogger(this);
    }

    /**
     * Can be modified by the extensions of the Game class (to change the default player setup)
     */
    void initializePlayers() {
        gipfBoardState.players = new PlayersInGame();
    }

    /**
     * Can be modified by the extensions of the Game class (to change the default board)
     */
    void initializeBoard() {
        this.gipfBoardState = new GipfBoardState();
    }

    /**
     * Checks whether the position is located on the playing area or the outer dots.
     *
     * @param p the position of which should be determined whether it is on the board
     */
    public boolean isPositionOnPlayAreaOrOuterDots(Position p) {
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
     * Checks whether the position is located on the inner board (the playing area). Returns false for positions on the
     * outer positions, as well as positions that are not on the board.
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

    private void movePiece(GipfBoardState gipfBoardState, Position currentPosition, int deltaPos) throws InvalidMoveException {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isOnInnerBoard(nextPosition)) {
            throw new InvalidMoveException();
        } else {
            try {
                if (gipfBoardState.getPieceMap().containsKey(nextPosition)) {
                    movePiece(gipfBoardState, nextPosition, deltaPos);
                }

                // Don't copy over null values, instead remove the value from the hashmap
                if (gipfBoardState.getPieceMap().containsKey(currentPosition)) {
                    gipfBoardState.getPieceMap().put(nextPosition, gipfBoardState.getPieceMap().remove(currentPosition));
                }
            } catch (InvalidMoveException e) {
                if (gipfBoardState == getGipfBoardState()) {
                    gameLogger.log("Moving to " + nextPosition.getName() + " is not allowed");
                }
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
        if (gipfBoardState.players.winner() != null) return;

        GipfBoardState newGipfBoardState = new GipfBoardState(gipfBoardState);  // If the move succeeds, newGipfBoardState will be the new gipfBoardState

        if (gipfBoardState.players.current().reserve >= move.addedPiece.getPieceValue()) {
            newGipfBoardState.getPieceMap().put(move.startPos, move.addedPiece);   // Add the piece to the board on the starting position

            try {
                movePiecesTowards(newGipfBoardState, move.startPos, move.direction);

                if (move.addedPiece.getPieceType() == PieceType.GIPF) {
                    gipfBoardState.players.current().hasPlacedGipfPieces = true;
                }

                boardHistory.add(gipfBoardState);
                gipfBoardState = newGipfBoardState;

                HashMap<PieceColor, Set<Line.Segment>> linesTakenBy = new HashMap<>();
                linesTakenBy.put(WHITE, new HashSet<>());
                linesTakenBy.put(BLACK, new HashSet<>());

                HashMap<PieceColor, Set<Position>> piecesBackTo = new HashMap<>();
                piecesBackTo.put(WHITE, new HashSet<>());
                piecesBackTo.put(BLACK, new HashSet<>());
                piecesBackTo.put(null, new HashSet<>());    // Hash maps can take null as a key, in this case used for pieces that are removed from the board

                if (!move.isCompleteMove) {
                    // Get the lines of the own color
                    removeLines(newGipfBoardState, gipfBoardState.players.current().pieceColor, linesTakenBy, piecesBackTo);

                    // Get lines of the opponent
                    PieceColor opponentColor = gipfBoardState.players.current().pieceColor == WHITE ? BLACK : WHITE;
                    removeLines(newGipfBoardState, opponentColor, linesTakenBy, piecesBackTo);
                } else {
                    piecesBackTo.get(WHITE).addAll(move.piecesToWhite);
                    piecesBackTo.get(BLACK).addAll(move.piecesToBlack);
                    piecesBackTo.get(null).addAll(move.piecesRemoved);

                }
                // Get the line segments that
                // Get the lines of the color of the other player

                gameLogger.log(move.toString());
                piecesBackTo.entrySet().forEach(e -> removePiecesFromBoard(newGipfBoardState, e.getValue()));

                for (PieceColor pieceColor : PieceColor.values()) {
                    if (piecesBackTo.get(pieceColor).size() != 0) {
                        gipfBoardState.players.get(pieceColor).reserve += piecesBackTo.get(pieceColor).size();

                        gameLogger.log(pieceColor + " retrieved " + piecesBackTo.get(pieceColor).size() + " pieces");
                    }
                }

                // Update for the last added piece
                gipfBoardState.players.current().reserve -= move.addedPiece.getPieceValue();

                if (getGameOverState()) {
                    gipfBoardState.players.updateCurrent();
                    gipfBoardState.players.makeCurrentPlayerWinner();

                    gameLogger.log("Game over! " + gipfBoardState.players.winner().pieceColor + " won!");
                } else {
                    gipfBoardState.players.updateCurrent();
                }

                if (!gipfBoardState.players.current().isPlacingGipfPieces) {
                    gipfBoardState.players.current().hasPlacedNormalPieces = true;
                }

            } catch (InvalidMoveException e) {
                System.out.println("Move not applied");
            }
        } else {
            gameLogger.log("No pieces left");
        }

        gipfBoardState.boardStateProperties.update();
    }

    public GipfBoardState getGipfBoardState() {
        return gipfBoardState;
    }

    public void setPlayer(PieceColor color, Class<? extends Function<GipfBoardState, Move>> player) {
        try {
            if (color == WHITE) whitePlayer = player.newInstance();
            if (color == BLACK) blackPlayer = player.newInstance();
        } catch (Exception e) {
            System.err.println("Could not instantiate player.");
            e.printStackTrace();
        }
    }

    /**
     * This method is currently a placeholder. Currently statically returns all potential candidates for allowed moves,
     * but it should be checked which ones are actually allowed.
     */
    public Set<Move> getAllowedMoves() {
        if (gipfBoardState.players.winner() != null) {
            return new HashSet<>();
        }

        Set<Piece> allowedStartPieces = new HashSet<>();
        allowedStartPieces.add(getCurrentPiece());
        if (getCurrentPiece().getPieceType() == GIPF) {
            allowedStartPieces.add(Piece.of(NORMAL, gipfBoardState.players.current().pieceColor));
        }

        Set<Move> potentialMoves = allowedStartPieces.stream().flatMap(
                piece -> getPotentialStartMoves(piece).stream()
        ).collect(toSet());

        potentialMoves.stream().forEach(m -> m.isCompleteMove = true);

        Set<Move> potentialMovesIncludingLineSegmentRemoval = new HashSet<>();
        for (Move potentialMove : potentialMoves) {
            GipfBoardState temporaryBoardState = new GipfBoardState(getGipfBoardState());
            try {
                movePiece(temporaryBoardState, potentialMove.getStartingPosition(), potentialMove.getDirection().getDeltaPos());

                Set<Line.Segment> removableLineSegmentsByCurrentPlayer = getRemovableLineSegments(temporaryBoardState, (gipfBoardState.players.current().pieceColor));
                if (removableLineSegmentsByCurrentPlayer.size() == 0) {
                    potentialMovesIncludingLineSegmentRemoval.add(potentialMove);
                } else {
                    for (Line.Segment removedSegment : removableLineSegmentsByCurrentPlayer) {
                        Move moveWithRemovedLineSegment = new Move(potentialMove);

                        Set<Position> piecesToCurrentPlayer = removedSegment.getOccupiedPositions(temporaryBoardState);
                        if (gipfBoardState.players.current().pieceColor == WHITE)
                            moveWithRemovedLineSegment.piecesToWhite = piecesToCurrentPlayer;
                        if (gipfBoardState.players.current().pieceColor == BLACK)
                            moveWithRemovedLineSegment.piecesToBlack = piecesToCurrentPlayer;
                        potentialMovesIncludingLineSegmentRemoval.add(moveWithRemovedLineSegment);
                    }
                }

            } catch (InvalidMoveException e) {
                // Don't add it to potentialMovesIncludingLineSegmentRemoval
            }
        }

        return potentialMovesIncludingLineSegmentRemoval;
    }

    /**
     * Gets the border positions (dots) by concatenating all the positions per border line. The border lines are hardcoded in this
     * method.
     *
     * @return a set of positions positioned just out of the play area
     */
    private Set<Position> getDots() {
        return Stream.concat(
                new Line(this, new Position('a', 1), SOUTH_EAST).getPositionsOnLine().stream(),
                Stream.concat(new Line(this, new Position('e', 1), NORTH_EAST).getPositionsOnLine().stream(),
                        Stream.concat(new Line(this, new Position('i', 1), NORTH).getPositionsOnLine().stream(),
                                Stream.concat(new Line(this, new Position('i', 5), NORTH_WEST).getPositionsOnLine().stream(),
                                        Stream.concat(
                                                new Line(this, new Position('e', 9), SOUTH_WEST).getPositionsOnLine().stream(),
                                                new Line(this, new Position('a', 5), SOUTH).getPositionsOnLine().stream()
                                        )
                                )
                        )
                )
        ).collect(toSet());
    }

    public Piece getCurrentPiece() {
        PlayersInGame.Player currentPlayer = gipfBoardState.players.current();
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
    private Set<Line.Segment> getRemovableLineSegments(GipfBoardState gipfBoardState, PieceColor pieceColor) {
        Set<Line.Segment> removableLines = new HashSet<>();
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
            for (; isPositionOnPlayAreaOrOuterDots(currentPosition) && endOfSegment == null; currentPosition = currentPosition.next(direction)) {
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
                    removableLines.add(new Line.Segment(this, startOfSegment, endOfSegment, direction));
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

    public void loadState(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    public void returnToPreviousBoard() {
        if (boardHistory.size() > 1) {
            gipfBoardState = boardHistory.pop();
            loadState(gipfBoardState);
            gameLogger.log("Returned to previous game state");
        }
    }

    public GameLogger getGameLogger() {
        return gameLogger;
    }

    private void removePiecesFromBoard(GipfBoardState gipfBoardState, Set<Position> positions) {
        for (Position position : positions) {
            gipfBoardState.getPieceMap().remove(position);
        }
    }

    private void removeLines(GipfBoardState gipfBoardState, PieceColor pieceColor, Map<PieceColor, Set<Line.Segment>> linesTakenBy, Map<PieceColor, Set<Position>> piecesBackTo) {
        Set<Line.Segment> intersectingSegments;
        Set<Line.Segment> segmentsNotRemoved = new HashSet<>();

        do {
            intersectingSegments = new HashSet<>();
            Set<Line.Segment> removableSegmentsThisPlayer = getRemovableLineSegments(gipfBoardState, pieceColor);
            for (Line.Segment segment : removableSegmentsThisPlayer) {
                // Remove the line segments that are not intersecting with other line segments of the set
                boolean intersectionFound = false;

                for (Line.Segment otherSegment : removableSegmentsThisPlayer) {
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
                Line.Segment segment = intersectingSegments.iterator().next();
                currentRemoveSelection = segment.getOccupiedPositions(gipfBoardState);

                int dialogResult = GipfBoardComponent.showConfirmDialog(gipfBoardState.players.current().pieceColor + ", do you want to remove " + segment.getOccupiedPositions(gipfBoardState).stream().map(Position::getName).sorted().collect(toList()) + "?", "Remove line segment");
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // Remove the line
                    linesTakenBy.get(pieceColor).add(segment);
                } else if (dialogResult == JOptionPane.NO_OPTION) {
                    // Don't remove the line
                    segmentsNotRemoved.add(segment);
                }
                currentRemoveSelection = new HashSet<>();
            }

            for (Line.Segment segment : linesTakenBy.get(pieceColor)) {
                Predicate<Map.Entry<Position, Piece>> isNormalPiece = entry -> entry.getValue().getPieceType() == NORMAL;
                Predicate<Map.Entry<Position, Piece>> isCurrentPlayersColor = entry -> entry.getValue().getPieceColor() == pieceColor;
                Predicate<Map.Entry<Position, Piece>> doesPlayerWantToRemoveGipf = entry -> {
                    currentRemoveSelection.add(entry.getKey());
                    int dialogResult = GipfBoardComponent.showConfirmDialog(gipfBoardState.players.current().pieceColor + ", do you want to remove the Gipf at " + entry.getKey().getName() + "?", "Remove Gipf");
                    currentRemoveSelection = new HashSet<>();
                    return dialogResult == JOptionPane.YES_OPTION;
                };

                Map<Position, Piece> piecesRemovedMap = segment.getOccupiedPositions(gipfBoardState).stream().collect(toMap(p -> p, p -> gipfBoardState.getPieceMap().get(p)));

                piecesBackTo.get(pieceColor).addAll(piecesRemovedMap.entrySet().stream()
                        .filter(isCurrentPlayersColor.and(isNormalPiece.or(doesPlayerWantToRemoveGipf)))
                        .map(Map.Entry::getKey)
                        .collect(toSet()));

                piecesBackTo.get(null).addAll(piecesRemovedMap.entrySet().stream()
                        .filter(isCurrentPlayersColor.negate().and(isNormalPiece.or(doesPlayerWantToRemoveGipf)))
                        .map(Map.Entry::getKey)
                        .collect(toSet()));
            }

            piecesBackTo.values()
                    .forEach(positionSet -> removePiecesFromBoard(gipfBoardState, positionSet));
        }
        while (intersectingSegments.size() > 0);


    }

    public Set<Position> getCurrentRemoveSelection() {
        return currentRemoveSelection;
    }

    /**
     * Cannot be called if winningPlayer is not null. Determines whether there is a winning player at this moment in the
     * game, and if so, set the winningPlayer pointer accordingly.
     *
     * @return true if the game over condition has been fulfilled, false otherwise.
     */
    protected abstract boolean getGameOverState();

    public void newGameLogger() {
        this.gameLogger = new GameLogger(this);
    }

    private Set<Move> getPotentialStartMoves(Piece piece) {
        return new HashSet<>(Arrays.asList(
                new Move(piece, new Position('a', 1), NORTH_EAST),
                new Move(piece, new Position('a', 2), NORTH_EAST),
                new Move(piece, new Position('a', 2), SOUTH_EAST),
                new Move(piece, new Position('a', 3), NORTH_EAST),
                new Move(piece, new Position('a', 3), SOUTH_EAST),
                new Move(piece, new Position('a', 4), NORTH_EAST),
                new Move(piece, new Position('a', 4), SOUTH_EAST),
                new Move(piece, new Position('a', 5), SOUTH_EAST),
                new Move(piece, new Position('b', 6), SOUTH),
                new Move(piece, new Position('b', 6), SOUTH_EAST),
                new Move(piece, new Position('c', 7), SOUTH),
                new Move(piece, new Position('c', 7), SOUTH_EAST),
                new Move(piece, new Position('d', 8), SOUTH),
                new Move(piece, new Position('d', 8), SOUTH_EAST),
                new Move(piece, new Position('e', 9), SOUTH),
                new Move(piece, new Position('f', 8), SOUTH_WEST),
                new Move(piece, new Position('f', 8), SOUTH),
                new Move(piece, new Position('g', 7), SOUTH_WEST),
                new Move(piece, new Position('g', 7), SOUTH),
                new Move(piece, new Position('h', 6), SOUTH_WEST),
                new Move(piece, new Position('h', 6), SOUTH),
                new Move(piece, new Position('i', 5), SOUTH_WEST),
                new Move(piece, new Position('i', 4), NORTH_WEST),
                new Move(piece, new Position('i', 4), SOUTH_WEST),
                new Move(piece, new Position('i', 3), NORTH_WEST),
                new Move(piece, new Position('i', 3), SOUTH_WEST),
                new Move(piece, new Position('i', 2), NORTH_WEST),
                new Move(piece, new Position('i', 2), SOUTH_WEST),
                new Move(piece, new Position('i', 1), NORTH_WEST),
                new Move(piece, new Position('h', 1), NORTH),
                new Move(piece, new Position('h', 1), NORTH_WEST),
                new Move(piece, new Position('g', 1), NORTH),
                new Move(piece, new Position('g', 1), NORTH_WEST),
                new Move(piece, new Position('f', 1), NORTH),
                new Move(piece, new Position('f', 1), NORTH_WEST),
                new Move(piece, new Position('e', 1), NORTH),
                new Move(piece, new Position('d', 1), NORTH),
                new Move(piece, new Position('d', 1), NORTH_EAST),
                new Move(piece, new Position('c', 1), NORTH),
                new Move(piece, new Position('c', 1), NORTH_EAST),
                new Move(piece, new Position('b', 1), NORTH),
                new Move(piece, new Position('b', 1), NORTH_EAST)
        ));
    }

    public void startGameCycle(Runnable finalAction) {
        if (automaticPlayThread == null || !automaticPlayThread.isAlive()) {
            GameLoopRunnable gameLoopRunnable = new GameLoopRunnable();
            gameLoopRunnable.finalAction = finalAction;
            automaticPlayThread = new Thread(gameLoopRunnable);
        }

        automaticPlayThread.start();
    }

    public void startGameCycle() {
        startGameCycle(null);
    }

    private class GameLoopRunnable implements Runnable {
        public Runnable finalAction;

        @Override
        public void run() {
            Move move;
            if (gipfBoardState.players.current() == gipfBoardState.players.white) {
                move = whitePlayer.apply(gipfBoardState);
            } else {
                move = blackPlayer.apply(gipfBoardState);
            }

            if (move != null) {
                applyMove(move);
            }

            // A final action to be executed (for example repainting the component)
            if (finalAction != null) {
                finalAction.run();
            }
        }
    }
}
