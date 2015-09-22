package GUI.GipfBoardComponent;

import java.awt.*;

/**
 * Singleton type class. Produces a single instance that will return values and objects related to the behaviour of the
 * program.
 * UIval stands for User Interface Values, but is shortened to reduce verbosity.
 *
 * Created by frans on 22-9-2015.
 */
public class UIval {
    private static UIval instance = null;    // Needed for singleton behaviour
    public final Stroke normalPieceStroke = new BasicStroke(4.0f);
    public final Stroke gipfPieceStroke = new BasicStroke(4.0f);
    public final int pieceSize = 50;                               // The size in pixels in which the pieces are displayed
    public final int hoverCircleSize = 20;
    public final Stroke hoverPositionStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);     // A dashed stroke style. Don't really know how this works.
    // Constants which can be changed to change the look
    public final int nrOfColumnsOnGipfBoard = 9;                              // The number of columns on a gipf board. Only edit if the GipfBoard class can handle it
    public final int nrOfRowsOnGipfBoard = 9;                                 // The number of rows on a gipf board. Only edit if the GipfBoard class can handle it
    public final int marginSize = 25;                                         // The margin on the sides of the board
    public final boolean antiAliasingEnabled = true;                  // Enable anti aliasing. If disabled, the drawing will be much faster. Can be disabled for performance
    public final int filledCircleSize = 15;                           // The size of the filled circles
    public final Stroke moveToArrowStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);
    public final Font positionNameFont = new Font("default", Font.BOLD, 14);

    // Colors
    public final Color backgroundColor = new Color(0xD2FF9B);            // The background of the component
    public final Color lineColor = new Color(0x8D8473);                  // The lines showing how pieces are allowed to move
    public final Color positionNameColor = lineColor;                    // Color of position names
    public final Color centerColor = new Color(0xE5FFCE);                // The hexagon in the center
    public final Color filledCircleColor = backgroundColor;              // Color of the circles that are filled (on the edges of the board)
    public final Color filledCircleBorderColor = new Color(0x7D8972);    // Border color of the filled circles
    public final Color moveToArrowColor = lineColor;                     // The line indicating where the player can move his piece
    public final Color whiteSingleColor = new Color(0xF9F9F9);           // Color of the normal white piece
    public final Color whiteGipfColor = whiteSingleColor;                // Color of the white gipf piece
    public final Color blackSingleColor = new Color(0x525252);           // Color of the normal black piece
    public final Color blackGipfColor = blackSingleColor;                // Color of the black gipf piece
    public final Color singlePieceBorderColor = Color.black;             // Border color of normal single pieces
    public final Color gipfPieceBorderColor = new Color(0xDA0000);       // Border color of gipf pieces
    public final Color hoverBorderColor = lineColor;           // The border color of positions that is hovered over
    public final Color hoverFillColor = backgroundColor;       // The filling color of positions that is hovered over

    protected UIval() {
        // Exists only to prohibit instantiation
    }

    public static UIval get() {
        if (instance == null) {
            instance = new UIval();
        }
        return instance;
    }
}
