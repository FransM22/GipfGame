package GameLogic;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;
import static GameLogic.PieceType.GIPF;
import static GameLogic.PieceType.NORMAL;

/**
 * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same pieceColor.
 * Created by frans on 5-10-2015.
 */
public enum Piece {
    WHITE_SINGLE,
    WHITE_GIPF,
    BLACK_SINGLE,
    BLACK_GIPF;

    @Override
    public String toString() {
        switch (this) {
            case WHITE_SINGLE:
                return "White Single";
            case WHITE_GIPF:
                return "White Gipf";
            case BLACK_SINGLE:
                return "Black Single";
            case BLACK_GIPF:
                return "Black Gipf";
            default:
                return "[Piece type not known]";
        }
    }

    public int getPieceValue() {
        return getPieceType() == GIPF ? 2 : 1;
    }

    /**
     * Returns the type of the piece (either normal or gipf)
     *
     * @return the type of the piece
     */
    public PieceType getPieceType() {
        if (this == WHITE_SINGLE || this == BLACK_SINGLE)
            return NORMAL;
        return GIPF;
    }

    /**
     * Returns the color of the piece (either black or white)
     *
     * @return the color of the piece
     */
    public PieceColor getPieceColor() {
        if (this == WHITE_SINGLE || this == WHITE_GIPF) {
            return WHITE;
        }
        return BLACK;
    }
}
