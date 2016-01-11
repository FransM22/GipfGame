package GameLogic.Game;

import AI.Players.ComputerPlayer;
import AI.Players.HumanPlayer;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI2.SettingsSingleton;
import GameLogic.*;
import javafx.util.Pair;

import javax.swing.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
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
 * <p>
 * Created by frans on 21-9-2015.
 */
public abstract class Game implements Serializable {
    private final BoardHistory boardHistory;            // Stores the history of the boards
    public ComputerPlayer whitePlayer;
    public ComputerPlayer blackPlayer;
    public Thread automaticPlayThread = new Thread();
    public int minWaitTime;
    GipfBoardState gipfBoardState;                              // The board where the pieces are stored.
    private GameLogger gameLogger;
    private int moveCounter;    // For debugging output
    private int runCounter;
    private Instant gameStartInstant = Instant.now();
    private Set<Position> currentRemoveSelection = new HashSet<>(); // Makes it possible for the gipfboardcomponent to display crosses on the pieces and lines that can be selected for removal

    Game() {
        /*
         * Initialize a new starting GipfBoardState object for this game. The initializePieceMap() and initializePlayers()
         * methods are meant to be overridden by the classes extending the game class.
         */
        this.gipfBoardState = new GipfBoardState(null, initializePieceMap(), initializePlayers());

        boardHistory = new BoardHistory();
        boardHistory.add(gipfBoardState);

        // Set pointers to the algorithms used for each player
        whitePlayer = new HumanPlayer();
        blackPlayer = new HumanPlayer();

        /*
         * Empty logger, for now it makes no sense to store all the log data in all generated games. String concatenations
         * are relatively expensive and the output is shown nowhere.
         */
        gameLogger = new EmptyLogger(this);
    }

    /**
     * Can be modified by the extensions of the Game class (to change the default player setup)
     */
    PlayersInGame initializePlayers() {
        return new PlayersInGame();
    }

    /**
     * Can be modified by the extensions of the Game class (to change the default board)
     */
    TreeMap<Position, Piece> initializePieceMap() {
        return new TreeMap<>();
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
     * Accessing this method is much faster than accessing getDots()
     *
     * @param p
     * @return
     */
    public boolean isDotPosition(Position p) {
        return isPositionOnPlayAreaOrOuterDots(p) && !isOnInnerBoard(p);
    }

    /**
     * Checks whether the position is located on the inner board (the playing area). Returns false for positions on the
     * outer positions, as well as positions that are not on the board.
     * <p>
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

    private void movePiece(Map<Position, Piece> pieceMap, Position currentPosition, int deltaPos) throws InvalidMoveException {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isOnInnerBoard(nextPosition)) {
            throw new InvalidMoveException();
        } else {
            try {
                if (pieceMap.containsKey(nextPosition)) {
                    movePiece(pieceMap, nextPosition, deltaPos);
                }

                // Don't copy over null values, instead remove the value from the hashmap
                if (pieceMap.containsKey(currentPosition)) {
                    pieceMap.put(nextPosition, pieceMap.remove(currentPosition));
                }
            } catch (InvalidMoveException e) {
                throw new InvalidMoveException();
            }
        }
    }

    private void movePiecesTowards(Map<Position, Piece> pieceMap, Position startPos, Direction direction) throws InvalidMoveException {
        int deltaPos = direction.getDeltaPos();

        Position currentPosition = new Position(startPos);

        try {
            movePiece(pieceMap, currentPosition, deltaPos);
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
        // An invalidMoveException can be thrown if applying that move would mean to place pieces on an illegal position.
        try {
            moveCounter++;
            // If there's already a winner, the move won't be applied
            if (gipfBoardState.players.winner() != null) return;

            /*
             * Prepare for creating a new child GipfBoardState. The pieceMap and playersInGame objects of the current
             * gipfBoardState are unmodifiable, so we have to create modifiable copies.
             * If the move turns out to be legal, a new child GipfBoardState will be generated, based on the modified
             * copies of the PieceMap and the PlayersInGame objects.
             */
            // The piece map returned by getPieceMap() is unmodifiable, so it has to be converted to a new (hash) map
            // the newPieceMap can be modified, after that a new GipfBoardState can be generated.
            Map<Position, Piece> newPieceMap = new HashMap<>(gipfBoardState.getPieceMap());

            // The same is true for the PlayersInGame object. It is unmodifiable, so a new instance has to be created
            // for the new board state.
            PlayersInGame newPlayers = new PlayersInGame(gipfBoardState.players);

            // If the current player has enough pieces left in the reserve to perform the move (1 for a normal move, 2 for
            // a Gipf move.)
            if (newPlayers.current().reserve >= move.addedPiece.getPieceValue()) {
                /*
                 * Move the piece
                 */
                // Each move adds a new piece to the board
                newPieceMap.put(move.startPos, move.addedPiece);

                // Move it into the direction determined by the move
                movePiecesTowards(newPieceMap, move.startPos, move.direction);

                /*
                 * Remove the lines and pieces that can be removed from the board
                 */
                // Create an object that keeps track of which piece is taken by whom. An EnumMap instead of a HashMap is
                // used, because all keys correspond with values from the PieceColor enum.
                Map<PieceColor, Set<Line.Segment>> linesTakenBy = new EnumMap<>(PieceColor.class);
                linesTakenBy.put(WHITE, new HashSet<>());
                linesTakenBy.put(BLACK, new HashSet<>());

                // Create an object that keeps track of which individual pieces are taken by whom.
                // A HashMap is used because it can handle null keys, in contrast with EnumMaps.
                Map<PieceColor, Set<Position>> piecesBackTo = new HashMap<>();
                piecesBackTo.put(WHITE, new HashSet<>());
                piecesBackTo.put(BLACK, new HashSet<>());
                piecesBackTo.put(null, new HashSet<>());    // Used for pieces that are removed from the board

                /*
                 * Distinguish between complete and incomplete moves.
                 *  - Complete moves:
                 *    are generated by the getAllowedMoves() method and contain all information about that move,
                 *    including a choice for which lines or gipf pieces will be removed.
                 *  - Incomplete moves:
                 *    are performed by human players. These moves don't contain the information of which pieces are
                 *    removed. This means that there may be user interaction required if the player must choose between
                 *    multiple lines or gipf pieces that can be removed.
                 */
                if (move.isCompleteMove) {
                    // Complete moves are the easiest to handle, the positions of pieces that are removed are already
                    // determined.
                    // This means that we only have to read the values for the pieces that are returned to each player
                    // into the piecesBackTo map.
                    piecesBackTo.get(WHITE).addAll(move.piecesToWhite);
                    piecesBackTo.get(BLACK).addAll(move.piecesToBlack);
                    piecesBackTo.get(null).addAll(move.piecesRemoved);
                } else {
                    // Now we have incomplete moves. This means that we have to remove the pieces that are required to
                    // be removed. If the player must choose between different pieces / lines, the removeLines method
                    // will ask the player to make a choice.

                    // Get the lines that are taken by the current player (retrieved from linesTakenBy) and store them
                    // in the piecesBackTo map. The opponent's pieces are stored in piecesBackTo.get(null), because they
                    // are removed from the board.
                    removeLines(newPieceMap, newPlayers.current().pieceColor, linesTakenBy, piecesBackTo);
                    //linesTakenBy.get(newPlayers.current().pieceColor).addAll(getRemovableLineSegments(newPieceMap, newPlayers.current().pieceColor));

                    // Get the lines that are taken by the opponent (retrieved from the linesTakenBy map), and store
                    // them in the piecesBackTo map. The current player's pieces are stored in piecesBackTO.get(null),
                    // because they are removed from the board.
                    PieceColor opponentColor = newPlayers.current().pieceColor == WHITE ? BLACK : WHITE;
                    /*
                     * TODO: REFACTOR, SEE ABOVE EXAMPLE!!!!!!!!!!!!!!!!!!!!!!!!!
                     * TODO: DOESNT WORK AS EXPECTED
                     */
                    removeLines(newPieceMap, opponentColor, linesTakenBy, piecesBackTo);
                    //linesTakenBy.get(opponentColor).addAll(getRemovableLineSegments(newPieceMap, opponentColor));
                }
                gameLogger.log(move.toString());

                // Each value in the piecesBackTo map is a set, and each element (position) of the sets of all values
                // is removed from the pieceMap.
                // The number of the returned pieces for each player are added to their reserve.
                for (Map.Entry<PieceColor, Set<Position>> removedPieces : piecesBackTo.entrySet()) {
                    if (removedPieces.getKey() != null) {
                        // Calculate the sum for the pieces returned to this player. Normal pieces have a value of 1,
                        // gipf pieces a value of 2 determined in Piece.getPieceValue().
                        int returnedPiecesSum = removedPieces.getValue().stream()
                                .mapToInt(position -> {
                                    // TODO will only work in basic game
                                    if (newPieceMap.containsKey(position))
                                        return newPieceMap.get(position).getPieceValue();
                                    else
                                        return 1;
                                }).sum();

                        newPlayers.get(removedPieces.getKey()).reserve += returnedPiecesSum;
                        gameLogger.log(removedPieces.getKey() + " retrieved " + returnedPiecesSum + " pieces");
                    }

                    // The pieces are not earlier removed from the board, because the returnedPiecesSum variable can
                    // only be set if all the pieces are still on the board.
                    removePiecesFromPieceMap(newPieceMap, removedPieces.getValue());
                }

                /*
                 * Set the properties for the player, based on the move
                 */
                if (move.addedPiece.getPieceType() == PieceType.GIPF) {
                    newPlayers.current().hasPlacedGipfPieces = true;
                }
                if (!newPlayers.current().isPlacingGipfPieces) {
                    newPlayers.current().hasPlacedNormalPieces = true;
                }

                // Update the current player's reserve for the last added piece
                newPlayers.current().reserve -= move.addedPiece.getPieceValue();

                /*
                 * Check whether it is game over
                 */
                // If we create a new GipfBoardState based on the calculated properties, will there be a game over situation?
                if (getGameOverState(new GipfBoardState(null, newPieceMap, newPlayers))) {
                    // If the current player causes a game over situation, the other player (updateCurrent()), will be
                    // the winner of the game.
                    newPlayers = newPlayers.updateCurrent().makeCurrentPlayerWinner();
                    gameLogger.log("Game over! " + newPlayers.winner().pieceColor + " won!");

                    if (moveCounter != 1) {
                        if (SettingsSingleton.getInstance().showExperimentOutput) {
                            String moveCountString = Integer.toString(moveCounter);
                            String durationString = Long.toString(Duration.between(gameStartInstant, Instant.now()).toMillis());
                            String winnerString = newPlayers.winner().pieceColor.toString();
                            String whiteAlgorithm = whitePlayer.getClass().getSimpleName();
                            String blackAlgorithm = blackPlayer.getClass().getSimpleName();

                            System.out.printf("%s; %s; %s; %s; %s\n", whiteAlgorithm, blackAlgorithm, moveCountString, durationString, winnerString);   // Output line + newline
                        }
                    }
                }


                // We don't need to update the current player if the game has ended
                if (newPlayers.winner() == null) {
                    newPlayers = newPlayers.updateCurrent();
                }

                // Create a new gipfBoardState, based on the calculated PieceMap and PlayersInGame objects.
                GipfBoardState newGipfBoardState = new GipfBoardState(gipfBoardState, newPieceMap, newPlayers);
                boardHistory.add(gipfBoardState);
                this.gipfBoardState = newGipfBoardState;
            } else {
                gameLogger.log("No pieces left");
            }

            // Recalculate the properties of this gipfBoardState
            gipfBoardState.boardStateProperties.updateBoardState();

        } catch (InvalidMoveException e) {
            System.out.println("Move not applied");
        }
    }

    public GipfBoardState getGipfBoardState() {
        return gipfBoardState;
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
            try {
                Map<Position, Piece> pieceMap = new HashMap<>(getGipfBoardState().getPieceMap());

                movePiece(pieceMap, potentialMove.getStartingPosition(), potentialMove.getDirection().getDeltaPos());

                Set<Line.Segment> removableLineSegmentsByCurrentPlayer = getRemovableLineSegments(pieceMap, potentialMove.addedPiece.getPieceColor());
                PieceColor opponentColor = potentialMove.addedPiece.getPieceColor() == WHITE ? BLACK : WHITE;
                Set<Line.Segment> removableLineSegmentsByOpponent = getRemovableLineSegments(pieceMap, opponentColor);
//                if (removableLineSegmentsByCurrentPlayer.size() == 0) {
//                    potentialMovesIncludingLineSegmentRemoval.add(potentialMove);
//                } else {
//                    for (Line.Segment removedSegment : removableLineSegmentsByCurrentPlayer) {
//                        Move moveWithRemovedLineSegment = new Move(potentialMove);
//
//                        Set<Position> piecesToCurrentPlayer = removedSegment.getOccupiedPositions(pieceMap);
//                        if (gipfBoardState.players.current().pieceColor == WHITE)
//                            moveWithRemovedLineSegment.piecesToWhite = piecesToCurrentPlayer;
//                        if (gipfBoardState.players.current().pieceColor == BLACK)
//                            moveWithRemovedLineSegment.piecesToBlack = piecesToCurrentPlayer;
//
//                        potentialMovesIncludingLineSegmentRemoval.add(moveWithRemovedLineSegment);
//                    }
//                }

                if (removableLineSegmentsByCurrentPlayer.size() != 0) {
                    for (Line.Segment removedSegment : removableLineSegmentsByCurrentPlayer) {
                        Move moveWithRemovedLineSegment = new Move(potentialMove);

                        Set<Position> piecesToCurrentPlayer = removedSegment.getOccupiedPositions(pieceMap);


                        if (gipfBoardState.players.current().pieceColor == WHITE) {
                            moveWithRemovedLineSegment.piecesToWhite = piecesToCurrentPlayer;
                        }
                        if (gipfBoardState.players.current().pieceColor == BLACK) {
                            moveWithRemovedLineSegment.piecesToBlack = piecesToCurrentPlayer;
                        }

                        potentialMovesIncludingLineSegmentRemoval.add(moveWithRemovedLineSegment);
                    }
                } else if (removableLineSegmentsByOpponent.size() != 0) {
                    for (Line.Segment removedSegment : removableLineSegmentsByOpponent) {
                        Move moveWithRemovedLineSegment = new Move(potentialMove);

                        Set<Position> piecesToOpponent = removedSegment.getOccupiedPositions(pieceMap);

                        if (gipfBoardState.players.current().pieceColor == WHITE) {
                            moveWithRemovedLineSegment.piecesToWhite = piecesToOpponent;
                        }
                        if (gipfBoardState.players.current().pieceColor == BLACK) {
                            moveWithRemovedLineSegment.piecesToWhite = piecesToOpponent;
                        }

                        potentialMovesIncludingLineSegmentRemoval.add(moveWithRemovedLineSegment);
                    }
                } else {
                    potentialMovesIncludingLineSegmentRemoval.add(potentialMove);
                }

                // TODO fix wrong piece removal code

            } catch (InvalidMoveException e) {
                // Don't add it to potentialMovesIncludingLineSegmentRemoval
            }
        }

        return potentialMovesIncludingLineSegmentRemoval;
    }

    private Set<List<Pair<PieceColor, Line.Segment>>> getRemovableLineSetOrderingsFromGipfBoard(GipfBoardState gipfBoardState, PieceColor currentPlayerColor) {
        Set removableLineSetOrderingsFromGipfboard = new HashSet<>();
        Map<Position, Piece> pieceMap = gipfBoardState.getPieceMap();

        for (Line.Segment lineSegment : getRemovableLineSegments(pieceMap, currentPlayerColor)) {
            // Line segments removable by the current player
            for (Position positionOnSegment : lineSegment.getOccupiedPositions(pieceMap)) {
                pieceMap.remove(positionOnSegment);

                Set segmentsRemovableFromChildBoard = getRemovableLineSegments(pieceMap, currentPlayerColor);
            }

            // Line segments removable by the current opponent
            for (Position positionOnSegment : lineSegment.getOccupiedPositions(pieceMap)) {
                pieceMap.remove(positionOnSegment);

                Set segmentsRemovableFromChildBoard = getRemovableLineSegments(pieceMap, currentPlayerColor);
            }
        }


        // cal recursively

        return null;
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
    private Set<Line.Segment> getRemovableLineSegments(Map<Position, Piece> pieceMap, PieceColor pieceColor) {
        Set<Line.Segment> removableLines = new HashSet<>();
        Set<Line> linesOnTheBoard = Line.getLinesOnTheBoard(this);      // Get all the possible lines on the board. Positions don't need to be occupied.

        for (Line line : linesOnTheBoard) {
            /* DEBUGGING CODE */
            if (line.getPositionsOnLine().contains(new Position('i', 5))) {
                int need_to_debug_here = 1;
            }
            /* END OF DEBUGGING CODE */

            Position currentPosition = line.getStartPosition();
            Position startOfSegment = null;
            Position endOfSegment = null;
            Direction direction = line.getDirection();
            int consecutivePieces = 0;                  // We start at a dot position, so we can assume that we don't start in a set of consecutive pieces
            boolean isInLineSegment = false;

            // Break the for-loop if an endOfSegment has been found (because the largest lines only have 7 positions on the board, there
            // can't be more than one set of four pieces of the same color (requiring at least 9 positions) on the board.
            for (; endOfSegment == null && isPositionOnPlayAreaOrOuterDots(currentPosition); currentPosition = currentPosition.next(direction)) {
                PieceColor currentPieceColor = pieceMap.containsKey(currentPosition) ? pieceMap.get(currentPosition).getPieceColor() : null;

                // Update the consecutivePieces
                if (currentPieceColor == pieceColor) consecutivePieces++;
                if (consecutivePieces == 4) isInLineSegment = true;
                if (currentPieceColor != pieceColor) consecutivePieces = 0;

                if (isInLineSegment) {
                    if (isDotPosition(currentPosition) || currentPieceColor == null) {
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

    private void removePiecesFromPieceMap(Map<Position, Piece> pieceMap, Set<Position> positions) {
        for (Position position : positions) {
            // An extra check. THe removelines method will remove pieces before
            if (pieceMap.containsKey(position))
                pieceMap.remove(position);
//            else
//                System.out.println("Can't remove " + position);
        }
    }

    // TODO: Refactor method
    private void removeLines(Map<Position, Piece> pieceMap, PieceColor pieceColor, Map<PieceColor, Set<Line.Segment>> linesTakenBy, Map<PieceColor, Set<Position>> piecesBackTo) {
        // TODO: method seems to recognize lines a move too late
        // TODO: lines are not properly removed from the piecemap
        Set<Line.Segment> intersectingSegments;
        Set<Line.Segment> segmentsNotRemoved = new HashSet<>();

        do {
            intersectingSegments = new HashSet<>();
            Set<Line.Segment> removableSegmentsThisPlayer = getRemovableLineSegments(pieceMap, pieceColor);
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
                currentRemoveSelection = segment.getOccupiedPositions(pieceMap);

                int dialogResult = GipfBoardComponent.showConfirmDialog(gipfBoardState.players.current().pieceColor + ", do you want to remove " + segment.getOccupiedPositions(pieceMap).stream().map(Position::getName).sorted().collect(toList()) + "?", "Remove line segment");
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

                Map<Position, Piece> piecesRemovedMap = segment.getOccupiedPositions(pieceMap).stream().collect(toMap(p -> p, p -> pieceMap.get(p)));

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
                    .forEach(positionSet -> removePiecesFromPieceMap(pieceMap, positionSet));
        }
        while (intersectingSegments.size() > 0);

        return;
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
    protected abstract boolean getGameOverState(GipfBoardState gipfBoardState);

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
        GameLoopRunnable gameLoopRunnable = new GameLoopRunnable();
        gameLoopRunnable.finalAction = finalAction;
        automaticPlayThread = new Thread(gameLoopRunnable);

        automaticPlayThread.start();
    }

    public void startNGameCycles(Runnable finalAction, int nrOfRuns) {
        Class currWhitePlayer = whitePlayer.getClass();
        Class currBlackPlayer = blackPlayer.getClass();

        automaticPlayThread = new Thread(() -> {
            for (int i = 0; i < nrOfRuns; i++) {
                GipfBoardState gipfBoardStateCopy = new GipfBoardState(getGipfBoardState(), gipfBoardState.getPieceMap(), gipfBoardState.players);
                Game copyOfGame = new BasicGame();
                try {
                    copyOfGame.whitePlayer = (ComputerPlayer) currWhitePlayer.newInstance();
                    copyOfGame.blackPlayer = (ComputerPlayer) currBlackPlayer.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                copyOfGame.loadState(gipfBoardStateCopy);

                // TODO move this to somewhere else
                if (SettingsSingleton.getInstance().showExperimentOutput) {
                    System.out.print(i + ": ");
                }
                GameLoopRunnable gameLoopRunnable = new GameLoopRunnable(copyOfGame);
                gameLoopRunnable.finalAction = finalAction;
                gameLoopRunnable.run();
            }
        });

        automaticPlayThread.start();
    }

    public void applyCurrentPlayerMove() throws GameEndException {
        Move move;

        if (gipfBoardState.players.current() == gipfBoardState.players.white) {
            move = whitePlayer.apply(gipfBoardState);
        } else {
            move = blackPlayer.apply(gipfBoardState);
        }

        if (move != null) {
            applyMove(move);
        } else {
            if (gipfBoardState.players.winner() != null) {
                throw new GameEndException();
            }
            // If the winning player is not yet defined, the game is still continuing.
            // (For example the human player is on turn)
        }
    }

    private class GameLoopRunnable implements Runnable {
        public Game game;
        public Runnable finalAction;

        public GameLoopRunnable() {
            game = Game.this;
        }

        public GameLoopRunnable(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            while (true) {  // This loop performs all moves in a game
                try {
                    // The waiting time is artificial, it makes the difference between two moves clearer
                    Thread.sleep(Game.this.minWaitTime);
                } catch (InterruptedException e) {
                    break;
                }

                try {
                    this.game.applyCurrentPlayerMove();
                } catch (GameEndException e) {
                    break;
                }

                // A final action to be executed (for example repainting the component)
                if (finalAction != null) {
                    finalAction.run();
                }
            }

        }
    }
}

