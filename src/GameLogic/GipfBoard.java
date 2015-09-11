package GameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game.
 *
 * Is still room for optimization, but should be done only if this code seems
 * to be a bottleneck.
 */
public class GipfBoard {
    // Each of these lists represents a vertical line on the gipf board
    List<Piece> a, b, c, d, e, f, g, h, i;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoard() {
        // Initialize the lists
        a = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        b = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        c = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        d = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        e = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        f = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        g = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        h = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
        i = new ArrayList<>(Arrays.asList(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY));
    }

    /**
     * Initialize a new Gipf board, with the same pieces on the same locations as an old board.
     * @param old board with pieces that should be copied
     */
    public GipfBoard(GipfBoard old) {
        a = new ArrayList<>(old.a);
        b = new ArrayList<>(old.b);
        c = new ArrayList<>(old.c);
        d = new ArrayList<>(old.d);
        e = new ArrayList<>(old.e);
        f = new ArrayList<>(old.f);
        g = new ArrayList<>(old.g);
        h = new ArrayList<>(old.h);
        i = new ArrayList<>(old.i);
    }

    public List<Piece> getCol(char col) {
        switch (col) {
            case 'a':
                return a;
            case 'b':
                return b;
            case 'c':
                return c;
            case 'd':
                return d;
            case 'e':
                return e;
            case 'f':
                return f;
            case 'g':
                return g;
            case 'h':
                return h;
            case 'i':
                return i;
        }

        System.err.println("ERROR: Column not found");
        return null;
    }

    public void setPiece(Position pos, Piece piece) {
        getCol(pos.col).set(pos.row - 1, piece);
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
                    return "[Piece not known]";
            }
        }
    }

    /**
     * The columns are stored with lowercase letters a..i, and the rows with numbers 1..9. This means we're following
     * the Gipf standards, and we're NOT starting from row 0.
     */
    public static class Position {
        char col;   // A letter (a, b, ..., i)
        short row;  // A number (1, 2, ..., 9)

        public Position(char col, int row) {
            this.col = col;
            this.row = (short) row;
        }
    }

    /*
     * TODO: Methods that still need to be implemented:
     *  - isValidMove(Move m)
     *  - applyMove(Move m)
     *  - getPiece(Position p)
     *  - setPiece(Position p)
     */
}
