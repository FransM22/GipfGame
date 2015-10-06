package GameLogic.Game;

import GameLogic.*;

import java.util.*;
import java.util.stream.Stream;

import static GameLogic.Piece.*;
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
    public Player whitePlayer = null;                           // The black and white player
    public Player blackPlayer = null;
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

                Map<Line, PieceColor> removableLines = detectFourPieces(newGipfBoardState); // Applied in normal game

                Set<Position> piecesBackToWhite = new HashSet<>();
                Set<Position> piecesBackToBlack = new HashSet<>();
                Set<Position> piecesRemoved = new HashSet<>();

                for (Map.Entry<Line, PieceColor> entry : removableLines.entrySet()) {
                    Line line = entry.getKey();
                    PieceColor rowColor = entry.getValue();
                    for (Position position : line) {
                        if (newGipfBoardState.getPieceMap().containsKey(position)) {
                            if (newGipfBoardState.getPieceMap().get(position).getPieceColor() == rowColor) {
                                if (rowColor == PieceColor.WHITE)
                                    piecesBackToWhite.add(position);
                                else if (rowColor == PieceColor.BLACK)
                                    piecesBackToBlack.add(position);
                            } else {
                                piecesRemoved.add(position);
                            }
                        }
                    }
                }

                // Remove the pieces
                move.removedPiecePositions = Stream.concat(Stream.concat(piecesBackToWhite.stream(), piecesBackToBlack.stream()), piecesRemoved.stream()).collect(toSet());
                move.removedPiecePositions.stream().forEach(newGipfBoardState.getPieceMap()::remove);

                gipfBoardState.whiteIsOnTurn = currentPlayer == whitePlayer;
                gipfBoardState.whitePiecesLeft = whitePlayer.piecesLeft;
                gipfBoardState.blackPiecesLeft = blackPlayer.piecesLeft;
                gipfBoardState.blackHasPlacedNormalPieces = blackPlayer.hasPlacedNormalPieces;
                gipfBoardState.whiteHasPlacedNormalPieces = whitePlayer.hasPlacedNormalPieces;

                whitePlayer.piecesLeft += piecesBackToWhite.size();
                blackPlayer.piecesLeft += piecesBackToBlack.size();

                gameLogger.log(move.toString());

                if (piecesBackToWhite.size() > 0) {
                    gameLogger.log(whitePlayer.pieceColor + " retrieved " + piecesBackToWhite.size() + " pieces");
                }
                if (piecesBackToBlack.size() > 0) {
                    gameLogger.log(blackPlayer.pieceColor + " retrieved " + piecesBackToBlack.size() + " pieces");
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
