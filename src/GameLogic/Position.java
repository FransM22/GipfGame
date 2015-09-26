package GameLogic;

/**
 * A position is determined by a position id. A position id can be converted to a col name (a..i) and a row
 * number (1..9), and vice versa.
 *
 * Created by frans on 17-9-2015.
 */

public final class Position {
    final int posId;    // Final, because a different position is supposed to be a different object

    /**
     * Set a position with only a position id (internal notation)
     *
     * @param posId The position id (value between 11 and 99) (see google doc why)
     */
    public Position(int posId) {
        this.posId = posId;
    }

    /**
     * Creates a new Position with the letter notation.
     *
     * @param col a column name between (and including) a - i
     * @param row a row number between (and including) 1 - 9
     */
    public Position(char col, int row) {
        if (col <= 'e') {
            posId = (((col - 'a' + 1) * 10) + row);
        } else {
            posId = (((col - 'a' + 1) * 10) + row + (col - 'e'));
        }
    }

    /**
     * Duplicate a position
     *
     * @param p the position that should be deleted
     */
    public Position(Position p) {
        this.posId = p.posId;
    }

    public static void main(String argv[]) {
        // Run only to test code
        System.out.println(new Position(15));
        System.out.println(new Position(59));
        System.out.println(new Position(87));
        System.out.println(new Position('a', 5));
        System.out.println(new Position('e', 9));
        System.out.println(new Position('h', 4));
    }

    /**
     * This method is used to get the position id (of the internal notation)
     */
    public int getPosId() {
        return posId;
    }

    /**
     * Get the column name in the letter notation
     */
    public char getColName() {
        // Get the column character from the position id.
        // The id is integer divided by 10 (so we get the 10's)
        // because the row numbers start at 1, 1 is subtracted,
        // and the letter 'a' is added, so we end up at the correct character
        return (char) ((posId / 10) - 1 + 'a');
    }

    /**
     * This method is used to get the row number in the letter notation
     */
    public int getRowNumber() {
        // The row number is equal to the last digit of the position id
        // which is obtained by calculating the modulo
        int colNr = posId / 10;

        if (colNr <= 5) {
            return posId % 10;
        } else {
            return (posId % 10) - colNr + 5;
        }
    }

    public String getName() {
        return "" + getColName() + getRowNumber();
    }

    @Override
    public String toString() {
        return "Pos: " + getColName() + getRowNumber() + " posId=(" + posId + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        return posId == position.posId;

    }

    public static int getDeltaPos(Position from, Position to) {
        return to.getPosId() - from.getPosId();
    }

    @Override
    public int hashCode() {
        return posId;
    }
}
