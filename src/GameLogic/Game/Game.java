package GameLogic.Game;

import GameLogic.*;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static GameLogic.Piece.*;

/**
 * The Game class controls whether the moves are according to the game rules, and if so, applies those moves to the board
 * <p/>
 * Created by frans on 21-9-2015.
 */
public abstract class Game {
    private final GameLogger gameLogger;
    private final List<GipfBoardState> boardHistory;            // Stores the history of the boards
    public Player whitePlayer = null;                           // The black and white player
    public Player blackPlayer = null;
    public boolean isGameOver = false;                          // Is only true if the game is finished
    GipfBoardState gipfBoardState;                              // The board where the pieces are stored.
    private final GameType gameType;                            // The game type (basic, standard, tournament)
    private Player currentPlayer;                               // Acts as a pointer to the current player
    private Player winningPlayer;                               // Acts as a pointer to the winning player

    Game(GameType gameType) {
        this.gameType = gameType;

        initializePlayers();
        initializeBoard();

        boardHistory = new ArrayList<>();
        boardHistory.add(gipfBoardState);

        currentPlayer = whitePlayer;

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

        if (currentPlayer.piecesLeft >= move.addedPiece.getPieceValue()) {
            setPiece(newGipfBoardState, move.startPos, move.addedPiece);   // Add the piece to the board on the starting position

            try {
                movePiecesTowards(newGipfBoardState, move.startPos, move.direction);

                Map<Position, Piece> removablePieces = detectFourPieces(newGipfBoardState); // Applied in normal game

                // Count how many pieces that can be removed are of the current player
                int nrOfPiecesBackToPlayer = removablePieces.values().stream().mapToInt(
                        piece ->
                                (piece.getPieceColor() == currentPlayer.pieceColor ? 1 : 0)
                                        * (piece.getPieceValue()))
                        .sum();

                // Remove the pieces
                move.removedPiecePositions = removablePieces.keySet();
                move.removedPiecePositions.stream().forEach(newGipfBoardState.getPieceMap()::remove);

                gipfBoardState.whiteIsOnTurn = currentPlayer == whitePlayer;
                gipfBoardState.whitePiecesLeft = whitePlayer.piecesLeft;
                gipfBoardState.blackPiecesLeft = blackPlayer.piecesLeft;
                gipfBoardState.blackHasPlacedNormalPieces = blackPlayer.hasPlacedNormalPieces;
                gipfBoardState.whiteHasPlacedNormalPieces = whitePlayer.hasPlacedNormalPieces;

                currentPlayer.piecesLeft += nrOfPiecesBackToPlayer;

                gameLogger.log(move.toString());

                if (nrOfPiecesBackToPlayer > 0) {
                    gameLogger.log(currentPlayer.pieceColor + " retrieved " + nrOfPiecesBackToPlayer + " pieces");
                }

                currentPlayer.piecesLeft -= move.addedPiece.getPieceValue();

                if (currentPlayer.piecesLeft == 0) {
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

    private Set<Position> getBorderPositions() {
        return new HashSet<>();
    }

    private void updateCurrentPlayer() {
        currentPlayer = ((currentPlayer == whitePlayer) ? blackPlayer : whitePlayer);
    }

    public Piece getCurrentPiece() {
        if (currentPlayer.pieceColor == PieceColor.WHITE && currentPlayer.isPlacingGipfPieces)
            return WHITE_GIPF;
        else if (currentPlayer.pieceColor == PieceColor.WHITE)
            return WHITE_SINGLE;
        else if (currentPlayer.pieceColor == PieceColor.BLACK && currentPlayer.isPlacingGipfPieces)
            return BLACK_GIPF;
        else if (currentPlayer.pieceColor == PieceColor.BLACK)
            return BLACK_SINGLE;

        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * By Dingding
     */
    private Map<Position, Piece> detectFourPieces(GipfBoardState gipfBoardState) {
        //Direction: South to North
        Map<Position, Piece> removablePieces = new HashMap<>();

        Set<Pair<Position, Direction>> lines = new HashSet<>();
        lines.add(new Pair<>(new Position('a', 1), Direction.NORTH_EAST));
        lines.add(new Pair<>(new Position('a', 2), Direction.NORTH_EAST));
        lines.add(new Pair<>(new Position('a', 3), Direction.NORTH_EAST));
        lines.add(new Pair<>(new Position('a', 4), Direction.NORTH_EAST));
        lines.add(new Pair<>(new Position('i', 4), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('i', 3), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('i', 2), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('i', 1), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('h', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('h', 1), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('g', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('g', 1), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('f', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('f', 1), Direction.NORTH_WEST));
        lines.add(new Pair<>(new Position('e', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('d', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('d', 1), Direction.NORTH_EAST));
        lines.add(new Pair<>(new Position('c', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('c', 1), Direction.NORTH_EAST));
        lines.add(new Pair<>(new Position('b', 1), Direction.NORTH));
        lines.add(new Pair<>(new Position('b', 1), Direction.NORTH_EAST));

        for (Pair<Position, Direction> entry : lines) {
            Position startPosition = entry.getKey();
            Position currentPosition = startPosition;
            Direction direction = entry.getValue();


            int consecutivePieces = 0;
            PieceColor consecutivePiecesColor = null;

            for (; isPositionOnBigBoard(currentPosition); currentPosition = new Position(currentPosition.getPosId() + direction.getDeltaPos())) {
                PieceColor currentPieceColor = null;

                if (gipfBoardState.getPieceMap().containsKey(currentPosition)) {
                    currentPieceColor = gipfBoardState.getPieceMap().get(currentPosition).getPieceColor();
                }

                if (currentPieceColor != consecutivePiecesColor) {
                    if (consecutivePiecesColor != null && consecutivePieces >= 4) {

                        // Remove the pieces on the same line
                        for (Position pieceToBeRemoved = startPosition; isPositionOnBigBoard(pieceToBeRemoved); pieceToBeRemoved = new Position(pieceToBeRemoved.getPosId() + direction.getDeltaPos())) {
                            // Add all the pieces on the line to the removablePieces list
                            if (gipfBoardState.getPieceMap().containsKey(pieceToBeRemoved))
                                removablePieces.put(pieceToBeRemoved, gipfBoardState.getPieceMap().get(pieceToBeRemoved));
                        }
                        break;
                    }

                    consecutivePiecesColor = currentPieceColor;
                    consecutivePieces = 1;
                } else {
                    consecutivePieces++;
                }
            }
        }

        return removablePieces;
    }

    public Set<Position> getStartPositionsForMoves() {
        return getAllowedMoves()
                .stream()
                .map(Move::getStartingPosition)
                .collect(Collectors.toSet());
    }

    public Set<Position> getMoveToPositionsForStartPosition(Position position) {
        return getAllowedMoves()
                .stream()
                .filter(m -> m.getStartingPosition().equals(position))
                .map(move -> new Position(
                        move.getStartingPosition().getPosId() + move.getDirection().getDeltaPos()))
                .collect(Collectors.toSet());
    }

    public void returnToPreviousBoard() {
        if (boardHistory.size() > 1 && !isGameOver) {
            gipfBoardState = boardHistory.get(boardHistory.size() - 1);
            currentPlayer = gipfBoardState.whiteIsOnTurn ? whitePlayer : blackPlayer;
            whitePlayer.piecesLeft = gipfBoardState.whitePiecesLeft;
            whitePlayer.hasPlacedNormalPieces = gipfBoardState.whiteHasPlacedNormalPieces;
            blackPlayer.piecesLeft = gipfBoardState.blackPiecesLeft;
            blackPlayer.hasPlacedNormalPieces = gipfBoardState.blackHasPlacedNormalPieces;

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
}
