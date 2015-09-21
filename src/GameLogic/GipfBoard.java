package GameLogic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game.
 * <p/>
 * Is still room for optimization, but should be done only if this code seems
 * to be a bottleneck.
 */
public class GipfBoard {
    private Map<Position, Piece> pieceMap;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoard() {
        // Initialize the lists
        pieceMap = new HashMap<>();
    }

    /**
     * Initialize a new Gipf board, with the same pieces on the same locations as an old board.
     *
     * @param old board with pieces that should be copied
     */
    public GipfBoard(GipfBoard old) {
        pieceMap = new HashMap<>(old.pieceMap);
    }

    public void setPiece(Position pos, Piece piece) {
        pieceMap.put(pos, piece);
    }

    public Map<Position, Piece> getPieceMap() {
        return pieceMap;
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
        m.removedPiecePositions.forEach(pieceMap::remove);
    }

    public int getDeltaPos(Move.Direction direction) {
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

    private void movePiecesTowards(Position startPos, Move.Direction direction) {
        int deltaPos = getDeltaPos(direction);

        Position currentPosition = new Position(startPos);

        try {
            movePiece(currentPosition, deltaPos);
        } catch (Exception e) {
            System.out.println("Piece is not moved");
        }
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

            pieceMap.put(nextPosition, pieceMap.remove(currentPosition));
        } catch (InvalidPositionException e) {
            System.out.println("Position " + nextPosition + " is invalid");
        }
    }

    private boolean isPositionEmpty(Position p) {
        return !pieceMap.containsKey(p);
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

    public static void main(String argv[]) {
        GipfBoard gb = new GipfBoard();

        Position invalidPos = new Position('a', 6);
        System.out.println(invalidPos + " is on board?" + gb.isPositionOnBoard(invalidPos));

        Position validPos = new Position('b', 6);
        System.out.println(validPos + " is on board?" + gb.isPositionOnBoard(validPos));

        Position validPos2 = new Position('e', 5);
        System.out.println(validPos2 + " is on board?" + gb.isPositionOnBoard(validPos2));
    }
}
