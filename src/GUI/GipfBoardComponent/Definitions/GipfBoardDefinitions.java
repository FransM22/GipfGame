package GUI.GipfBoardComponent.Definitions;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public class GipfBoardDefinitions {
    public final static Stroke normalPieceStroke = new BasicStroke(4.0f);
    public final static Stroke gipfPieceStroke = new BasicStroke(4.0f);
    public final static int pieceSize = 50;                               // The size in pixels in which the pieces are displayed
    public final static int hoverCircleSize = 20;
    public final static Stroke hoverPositionStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);     // A dashed stroke style. Don't really know how this works.

    // Constants which can be changed to change the look
    public final static int nrOfColumnsOnGipfBoard = 9;                              // The number of columns on a gipf board. Only edit if the GipfBoard class can handle it
    public final static int nrOfRowsOnGipfBoard = 9;                                 // The number of rows on a gipf board. Only edit if the GipfBoard class can handle it
    public final static int marginSize = 25;                                         // The margin on the sides of the board

    public final static boolean antiAliasingEnabled = true;                  // Enable anti aliasing. If disabled, the drawing will be much faster. Can be disabled for performance
    public final static int filledCircleSize = 15;                           // The size of the filled circles

    public final static Stroke moveToArrowStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);
    public final static Font positionNameFont = new Font("default", Font.BOLD, 14);
}
