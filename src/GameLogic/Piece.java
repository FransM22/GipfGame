package GameLogic;

/**
 * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same pieceColor.
 * Created by frans on 5-10-2015.
 */
public enum Piece {
    WHITE_SINGLE,
    WHITE_GIPF,
    BLACK_SINGLE,
    BLACK_GIPF;

    private String value;

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

    public int getPieceValue() {
        return getPieceType() == PieceType.GIPF ? 2 : 1;
    }

    /**
     * Returns the type of the piece (either normal or gipf)
     *
     * @return the type of the piece
     */
    public PieceType getPieceType() {
        if (value == "WHITE_SINGLE" || value == "BLACK_SINGLE")
            return PieceType.NORMAL;
        return PieceType.GIPF;
    }
}
