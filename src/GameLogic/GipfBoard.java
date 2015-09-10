package GameLogic;

import java.util.ArrayList;
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
        a = new ArrayList<>(5);
        b = new ArrayList<>(6);
        c = new ArrayList<>(7);
        d = new ArrayList<>(8);
        e = new ArrayList<>(9);
        f = new ArrayList<>(8);
        g = new ArrayList<>(7);
        h = new ArrayList<>(6);
        i = new ArrayList<>(5);
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

    public List<Piece> getRow(char row) {
        switch (row) {
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

        System.err.println("ERROR: Row not found");
        return null;
    }

    /**
     * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same color.
     */
    public enum Piece {
        WHITE_SINGLE,
        WHITE_GIPF,
        BLACK_SINGLE,
        BLACK_GIPF
    }

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
     *  - getRow(char row)
     *  - getPiece(Position p)
     */
}
