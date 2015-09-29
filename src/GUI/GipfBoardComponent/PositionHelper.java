package GUI.GipfBoardComponent;

import GUI.UIval;
import GameLogic.Position;

/**
 * Is used to convert a position on the board to a position on the GipfBoardComponent and the other way around.
 *
 * Created by frans on 22-9-2015.
 */
public class PositionHelper {
    private final int componentWidth;
    private final int componentHeight;
    private final int marginSize;
    private final int nrOfRowsOnGipfBoard;
    private final int nrOfColumnsOnGipfBoard;

    public PositionHelper(GipfBoardComponent gipfBoardComponent) {
        componentWidth = gipfBoardComponent.getWidth();
        componentHeight = gipfBoardComponent.getHeight();
        marginSize = UIval.get().marginSize;
        nrOfRowsOnGipfBoard = UIval.get().nrOfRowsOnGipfBoard;
        nrOfColumnsOnGipfBoard = UIval.get().nrOfColumnsOnGipfBoard;
    }

    public int positionToScreenY(Position p) {
        int height = componentHeight - (2 * marginSize);
        int colNumber = p.getColName() - 'a' + 1;               // Column number, starting at 1
        double rowHeight = height / (nrOfRowsOnGipfBoard - 1);  // The first and last piece are shown at the beginning and end, so we only need nrOfRows - 1 equally divided rows

        if (colNumber <= 5) {
            return (int) Math.round(height - (p.getRowNumber() - 1 - 0.5 * (colNumber - 5)) * rowHeight) + marginSize;
        } else {
            return (int) Math.round(height - (p.getRowNumber() - 1 + 0.5 * (colNumber - 5)) * rowHeight) + marginSize;
        }
    }

    public int positionToScreenX(Position p) {
        int width = componentWidth - (2 * marginSize);
        // nrOfColumns - 1, because n columns are  divided by n - 1 equal spaces
        return (p.getColName() - 'a') * (width / (nrOfColumnsOnGipfBoard - 1)) + marginSize;
    }

    Position screenCoordinateToPosition(int screenX, int screenY) {
        // Calculate the column and row sizes
        int columnWidth = (componentWidth - (2 * marginSize)) / (nrOfColumnsOnGipfBoard - 1);
        int rowHeight = (componentHeight - (2 * marginSize)) / (nrOfRowsOnGipfBoard - 1);

        // Take into account the margins. Also flip the y coordinate so we can access the board coordinates start from
        // row 1.
        int xOnBoard = screenX - marginSize;
        int yOnBoard = componentHeight - screenY - marginSize;

        int columnNumber = (int) ((Math.round((double) xOnBoard / columnWidth)));   // The column number, starting at 0 (!)
        char columnName = (char) (columnNumber + 'a');

        // These numbers do not take into account that the rows are not horizontally. This means that the result is only
        // correct for the middle column
        double rowNumberStartFromBottom = (double) yOnBoard / rowHeight;
        int horizontalDistanceFromCenter = Math.abs(screenX - (componentWidth / 2));

        double columnsFromCenter = (double) horizontalDistanceFromCenter / columnWidth;

        // This row number is the correct row number according to the letter notation (a3). This means that the first row
        // is number 1.
        double rowNumberFixed = (rowNumberStartFromBottom - (0.5 * columnsFromCenter)) + 1;
        int rowNumberInt = (int) Math.round(rowNumberFixed);

        return new Position(columnName, rowNumberInt);
    }
}
