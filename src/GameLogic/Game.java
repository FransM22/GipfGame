package GameLogic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Is still room for optimization, but should be done only if this code seems
 * to be a bottleneck.
 * <p/>
 * Created by frans on 21-9-2015.
 */
public class Game {
    private GipfBoard gipfBoard;

    public Game() {
        gipfBoard = new GipfBoard();
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
                row >= 10 ||
                row - col >= 5 ||
                col <= 0);
    }

    private boolean isPositionEmpty(Position p) {
        return !gipfBoard.getPieceMap().containsKey(p);
    }

    private void movePiece(Position currentPosition, int deltaPos) throws Exception {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isPositionOnBoard(nextPosition)) {
            throw new InvalidPositionException();
        }

        try {
            if (!isPositionEmpty(nextPosition)) {
                movePiece(nextPosition, deltaPos);
            }

            gipfBoard.getPieceMap().put(nextPosition, gipfBoard.getPieceMap().remove(currentPosition));
        } catch (InvalidPositionException e) {
            System.out.println("Position " + nextPosition + " is invalid");
        }
    }

    private void movePiecesTowards(Position startPos, Move.Direction direction) {
        int deltaPos = getDeltaPosFromDirection(direction);

        Position currentPosition = new Position(startPos);

        try {
            movePiece(currentPosition, deltaPos);
        } catch (Exception e) {
            System.out.println("Piece is not moved");
        }
    }

    public int getDeltaPosFromDirection(Move.Direction direction) {
        // Determine the deltaPos value based on the direction
        switch (direction) {
            case NORTH:
                return 1;
            case NORTH_EAST:
                return 11;
            case SOUTH_EAST:
                return 10;
            case SOUTH:
                return -1;
            case SOUTH_WEST:
                return -11;
            case NORTH_WEST:
                return -10;
        }

        return -1;
    }

    public Move.Direction getDirectionFromDeltaPos(int deltaPos) {
        switch (deltaPos) {
            case 1:
                return Move.Direction.NORTH;
            case 11:
                return Move.Direction.NORTH_EAST;
            case 10:
                return Move.Direction.SOUTH_EAST;
            case -1:
                return Move.Direction.SOUTH;
            case -11:
                return Move.Direction.SOUTH_WEST;
            case -10:
                return Move.Direction.NORTH_WEST;

            default:
                System.out.println("invalid deltaPos '" + deltaPos + "'");
                return null;
        }
    }

    /**
     * applyMove applies the given move to the board.
     * First, the new piece is added to the startPos
     * Then the pieces are moved in the direction of the move,
     * and finally pieces that need to be removed are removed from the board
     *
     * @param m the move that is applied
     */
    public void applyMove(Move m) {
        // Add the piece to the new pieces
        setPiece(m.startPos, m.addedPiece);

        movePiecesTowards(m.startPos, m.direction);

        // Remove the pieces that need to be removed
        // Java 8 solution (performs the remove operation on each of the pieces that should be removed)
        m.removedPiecePositions.forEach(gipfBoard.getPieceMap()::remove);
    }

    public void setPiece(Position pos, Game.Piece piece) {
        gipfBoard.getPieceMap().put(pos, piece);
    }

    public GipfBoard getGipfBoard() {
        return gipfBoard;
    }

    /**
     * This method is currently a placeholder. Should return all moves that are allowed in this game TODO
     *
     * @return
     */
    public Set<Move> getAllowedMoves() {
        return new HashSet<>(Arrays.asList(
                new Move(Piece.WHITE_GIPF, new Position('a', 1), Move.Direction.NORTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 2), Move.Direction.NORTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 2), Move.Direction.SOUTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 3), Move.Direction.NORTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 3), Move.Direction.SOUTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 4), Move.Direction.NORTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 4), Move.Direction.SOUTH_EAST),
                new Move(Piece.WHITE_GIPF, new Position('a', 5), Move.Direction.SOUTH_EAST)
        ));
    }

    /**
     * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same color.
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

    /*
     * TODO: Methods that still need to be implemented:
     *  - isValidMove(Move m)
     *  * method to get all the allowed moves from a specific board
     */
}
