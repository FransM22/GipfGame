package GUI;

import GameLogic.Position;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Singleton type class. Produces a single instance that will return values and objects related to the behaviour of the
 * program.
 * UIval stands for User Interface Values, but is shortened to reduce verbosity.
 * <p/>
 * Created by frans on 22-9-2015.
 */
public class UIval {
    private static UIval instance = null;    // Needed for singleton behaviour

    // Constants which can be changed to change the look
    public final Stroke normalPieceStroke = new BasicStroke(4.0f);
    public final int pieceSize = 50;                               // The size in pixels in which the pieces are displayed
    public final int hoverCircleSize = 15;
    public final Stroke hoverPositionStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);     // A dashed stroke style. Don't really know how this works.
    public final int nrOfColumnsOnGipfBoard = 9;                      // The number of columns on a gipf board. Only edit if the GipfBoard class can handle it
    public final int nrOfRowsOnGipfBoard = 9;                         // The number of rows on a gipf board. Only edit if the GipfBoard class can handle it
    public final int marginSize = 25;                                 // The margin on the sides of the board
    public final boolean antiAliasingEnabled = true;                  // Enable anti aliasing. If disabled, the drawing will be much faster. Can be disabled for performance
    public final int filledCircleSize = 15;                           // The size of the filled circles
    public final Stroke moveToArrowStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);
    public final Font positionNameFont = new Font("default", Font.BOLD, 14);
    public final Font gameOverFont = new Font("default", Font.BOLD, 40);

    // Interval times
    public final long hoverUpdateIntervalMs = 100;                    // The interval in ms of updating the position over which is being hovered
    public final long gameStateUpdateIntervalMs = 100;

    // Colors
    public final Color backgroundColor = new Color(0xD2FF9B);            // The background of the component
    public final Color lineColor = new Color(0x8D8473);                  // The lines showing how pieces are allowed to move
    public final Color positionNameColor = lineColor;                    // PieceColor of position names
    public final Color centerColor = new Color(0xE5FFCE);                // The hexagon in the center
    public final Color filledCircleColor = backgroundColor;              // PieceColor of the circles that are filled (on the edges of the board)
    public final Color filledCircleBorderColor = new Color(0x7D8972);    // Border color of the filled circles
    public final Color moveToArrowColor = new Color(0x80808080);                     // The line indicating where the player can move his piece
    public final Color whitePieceColor = new Color(0xF9F9F9);           // PieceColor of the normal white piece
    public final Color blackPieceColor = new Color(0x525252);           // PieceColor of the normal black piece
    public final Color singlePieceBorderColor = Color.black;             // Border color of normal single pieces
    public final Color gipfPieceBorderColor = new Color(0xDA0000);       // Border color of gipf pieces
    public final Color hoverBorderColor = new Color(0x0, true);           // The border color of positions that is hovered over
    public final Color hoverFillColor = new Color(0x80808080, true);       // The filling color of positions that is hovered over

    // These mark the center hexagon on the board
    public final Position[] centerCornerPositions = {            // Contains the corners of the center hexagon. Distinguishes the part where pieces can end up from the background
            new Position('b', 2),
            new Position('b', 5),
            new Position('e', 8),
            new Position('h', 5),
            new Position('h', 2),
            new Position('e', 2)
    };
    private final Position[] topAndBottomPositions = {
            new Position('a', 1),
            new Position('b', 1),
            new Position('c', 1),
            new Position('d', 1),
            new Position('e', 1),
            new Position('f', 1),
            new Position('g', 1),
            new Position('h', 1),
            new Position('i', 1),
            new Position('a', 5),
            new Position('b', 6),
            new Position('c', 7),
            new Position('d', 8),
            new Position('e', 9),
            new Position('f', 8),
            new Position('g', 7),
            new Position('h', 6),
            new Position('i', 5)
    };
    private final Position[] sidePositions = {
            new Position('a', 2),
            new Position('a', 3),
            new Position('a', 4),
            new Position('i', 2),
            new Position('i', 3),
            new Position('i', 4)
    };
    // These positions are named on the board
    public final Position[] namedPositionsOnBoard = topAndBottomPositions;
    // These positions have a circle on their position
    // Code concatenates two arrays via streams, see http://stackoverflow.com/a/23188881
    public final Position[] filledCirclePositions = Stream.concat(Arrays.stream(topAndBottomPositions), Arrays.stream(sidePositions)).toArray(Position[]::new);
    public Color gameOverTextColor = new Color(0xFF0000);

    private UIval() {
        // Exists only to prohibit instantiation
    }

    public static UIval get() {
        if (instance == null) {
            instance = new UIval();
        }
        return instance;
    }
}
