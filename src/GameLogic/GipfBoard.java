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
     * @param m
     */
    public void applyMove(Move m) {
        // Add the piece to the new pieces
        setPiece(m.startPos, m.addedPiece);

        movePiecesTowards(m.startPos, m.direction);

        // Remove the pieces that need to be removed
        for (Position p : m.removedPiecePositions) {
            pieceMap.remove(p);
        }
    }

    private void movePiecesTowards(Position startPos, Move.Direction direction) {

        // Determine the deltaPos value based on the direction
        int deltaPos = 0;   // We need an initial value
        switch (direction) {
            case NORTH:
                deltaPos = 1;
                break;
            case NORTH_EAST:
                deltaPos = 11;
                break;
            case SOUTH_EAST:
                deltaPos = 10;
                break;
            case SOUTH:
                deltaPos = -1;
                break;
            case SOUTH_WEST:
                deltaPos = -11;
                break;
            case NORTH_WEST:
                deltaPos = -10;
                break;
        }


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
     * @param p
     */
    private boolean isPositionOnBoard(Position p) {
        int col = p.getColName() - 'a' + 1;
        int row = p.getRowNumber();

        // See google doc for explanation of the formula
        if (row <= 0 ||
                row - col <= -5 ||
                col >= 10 ||
                row >= 10 ||
                row - col >= 5 ||
                col <= 0) {
            return false;
        }

        return true;
    }

    /**
     * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same color.
     */
    public enum Piece {
        EMPTY,          // .
        WHITE_SINGLE,   // w
        WHITE_GIPF,     // W
        BLACK_SINGLE,   // b
        BLACK_GIPF;     // B

        @Override
        public String toString() {
            switch (super.name()) {
                case "EMPTY":
                    return ".";
                case "WHITE_SINGLE":
                    return "w";
                case "WHITE_GIPF":
                    return "W";
                case "BLACK_SINGLE":
                    return "b";
                case "BLACK_GIPF":
                    return "B";
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
