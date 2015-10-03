package GameLogic;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Is still room for optimization, but should be done only if this code seems
 * to be a bottleneck.
 * <p/>
 * Created by frans on 21-9-2015.
 */
public class Game {
    public LinkedList<String> debugMessages;
    public Player whitePlayer;
    public Player blackPlayer;
    public boolean isGameOver = false;
    Player currentPlayer;
    Player winningPlayer;
    private GipfBoard gipfBoard;

    public Game() {
        gipfBoard = new GipfBoard();
        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        currentPlayer = whitePlayer;
        debugMessages = new LinkedList<>();
    }

    public static PieceColor getPieceColor(Piece piece) {
        if (piece == null) {
            return null;
        }

        switch (piece) {
            case WHITE_SINGLE:
            case WHITE_GIPF:
                return PieceColor.WHITE;
        }
        return PieceColor.BLACK;
    }

    public static PieceType getPieceType(Piece piece) {
        switch (piece) {
            case WHITE_SINGLE:
            case BLACK_SINGLE:
                return PieceType.NORMAL;
        }
        return PieceType.GIPF;
    }

    /**
     * Checks whether the position is located on the board
     *
     * @param p the position of which should be determined whether it is empty
     */
    public boolean isPositionOnBoard(Position p) {
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
     * By Leroy
     *
     * @param p
     * @return
     */
    //This method check valid Position
    public boolean isValidPosition(Position p) {
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

    private boolean isPositionEmpty(Position p) {
        return !gipfBoard.getPieceMap().containsKey(p);
    }

    private void movePiece(Position currentPosition, int deltaPos) throws Exception {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isValidPosition(nextPosition)) {
            throw new InvalidMoveException();
        } else {
            try {
                if (!isPositionEmpty(nextPosition)) {
                    movePiece(nextPosition, deltaPos);
                }

                gipfBoard.getPieceMap().put(nextPosition, gipfBoard.getPieceMap().remove(currentPosition));
            } catch (InvalidMoveException e) {
                debugOutput("Moving to " + nextPosition.getName() + " is not allowed");
                throw new InvalidMoveException();
            }
        }
    }

    private void movePiecesTowards(Position startPos, Direction direction) throws InvalidMoveException {
        int deltaPos = direction.getDeltaPos();

        Position currentPosition = new Position(startPos);

        try {
            movePiece(currentPosition, deltaPos);
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
        if (currentPlayer.piecesLeft >= 1) {
            // Add the piece to the new pieces
            setPiece(move.startPos, move.addedPiece);

            try {
                movePiecesTowards(move.startPos, move.direction);

                // Remove the pieces that need to be removed
                // Java 8 solution (performs the remove operation on each of the pieces that should be removed)


                // TODO Add pieces retrieved by removing pieces here

                move.removedPiecePositions.forEach(gipfBoard.getPieceMap()::remove);
                Map removablePieces = detectFourPieces(); // Applied in normal game
                removablePieces.keySet().stream().forEach(gipfBoard.getPieceMap()::remove);


                debugOutput(move.toString());
                currentPlayer.piecesLeft--;

                if (currentPlayer.piecesLeft == 0) {
                    updateCurrentPlayer();
                    isGameOver = true;
                    winningPlayer = currentPlayer;

                    debugOutput("Game over! " + winningPlayer.pieceColor + " won!");
                } else {
                    updateCurrentPlayer();
                }

            } catch (InvalidMoveException e) {
                gipfBoard.getPieceMap().remove(move.startPos);
                System.out.println("Move not applied");
            }
        } else {
            debugOutput("No pieces left");
        }
    }

    public void setPiece(Position pos, Game.Piece piece) {
        gipfBoard.getPieceMap().put(pos, piece);
    }

    public GipfBoard getGipfBoard() {
        return gipfBoard;
    }

    /**
     * This method is currently a placeholder. Currently statically returns all potential candidates for allowed moves,
     * but it should be checked which ones are actually allowed.
     *
     * @return
     */
    public Set<Move> getAllowedMoves() {
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

    private void updateCurrentPlayer() {
        currentPlayer = ((currentPlayer == whitePlayer) ? blackPlayer : whitePlayer);
    }

    public Piece getCurrentPiece() {
        if (currentPlayer.pieceColor == PieceColor.WHITE) return Piece.WHITE_SINGLE;
        if (currentPlayer.pieceColor == PieceColor.BLACK) return Piece.BLACK_SINGLE;

        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Set<Position> getBorderPositions() {
        // TODO
        return null;
    }

    /**
     * By Dingding
     */
    public Map<Position, Piece> detectFourPieces() {
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
            Position currentPosition = entry.getKey();
            Direction direction = entry.getValue();

            int consecutivePieces = 0;
            PieceColor consecutivePiecesColor = null;

            for ( ; isPositionOnBoard(currentPosition); currentPosition = new Position(currentPosition.getPosId() + direction.getDeltaPos())) {
                PieceColor currentPieceColor = getPieceColor(getGipfBoard().getPieceMap().get(currentPosition));

                if (currentPieceColor != consecutivePiecesColor) {
                    if (consecutivePiecesColor != null && consecutivePieces >= 4) {
                        for (int i = 1; i <= consecutivePieces; i++) {
                            Position removablePosition = new Position(currentPosition.getPosId() - (i * direction.getDeltaPos()));
                            removablePieces.put(removablePosition, getGipfBoard().getPieceMap().get(removablePosition));
                        }
                    }

                    consecutivePiecesColor = currentPieceColor;
                    consecutivePieces = 1;
                }
                else {
                    consecutivePieces++;
                }
            }
        }

        return removablePieces;
    }

    public void debugOutput(String debug) {
        debugMessages.add(debug);
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

    public enum PieceType {
        GIPF,
        NORMAL,
    }

    /**
     * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same pieceColor.
     */
    public enum Piece {
        WHITE_SINGLE,
        WHITE_GIPF,
        BLACK_SINGLE,
        BLACK_GIPF;

        @Override
        public String toString() {
            switch (super.name()) {
                case "WHITE_SINGLE":
                    return "White Single";
                case "WHITE_GIPF":
                    return "White Gipf";
                case "BLACK_SINGLE":
                    return "Black Single";
                case "BLACK_GIPF":
                    return "Black Gipf";
                default:
                    return "[Piece type not known]";
            }
        }
    }

    public enum PieceColor {
        WHITE,
        BLACK
    }

    public class Player {
        public PieceColor pieceColor;
        public int piecesLeft = 18;    // Each player starts with 18 pieces
        boolean isPlacingGipfPieces = true;

        Player(PieceColor pieceColor) {
            this.pieceColor = pieceColor;
        }
    }
}
