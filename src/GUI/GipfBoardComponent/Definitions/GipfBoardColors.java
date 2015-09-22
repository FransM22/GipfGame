package GUI.GipfBoardComponent.Definitions;

import java.awt.*;

/**
 * Class is used to have a single overview where all the colors are defined
 * <p/>
 * Created by frans on 22-9-2015.
 */
public final class GipfBoardColors {
    public final static Color backgroundColor = new Color(0xD2FF9B);            // The background of the component
    public final static Color lineColor = new Color(0x8D8473);                  // The lines showing how pieces are allowed to move
    public final static Color positionNameColor = lineColor;                    // Color of position names
    public final static Color centerColor = new Color(0xE5FFCE);                // The hexagon in the center
    public final static Color filledCircleColor = backgroundColor;              // Color of the circles that are filled (on the edges of the board)
    public final static Color filledCircleBorderColor = new Color(0x7D8972);    // Border color of the filled circles
    public final static Color moveToArrowColor = lineColor;                     // The line indicating where the player can move his piece
    public final static Color whiteSingleColor = new Color(0xF9F9F9);           // Color of the normal white piece
    public final static Color whiteGipfColor = whiteSingleColor;                // Color of the white gipf piece
    public final static Color blackSingleColor = new Color(0x525252);           // Color of the normal black piece
    public final static Color blackGipfColor = blackSingleColor;                // Color of the black gipf piece
    public final static Color singlePieceBorderColor = Color.black;             // Border color of normal single pieces
    public final static Color gipfPieceBorderColor = new Color(0xDA0000);       // Border color of gipf pieces
    public final static Color hoverBorderColor = GipfBoardColors.lineColor;           // The border color of positions that is hovered over
    public final static Color hoverFillColor = GipfBoardColors.backgroundColor;       // The filling color of positions that is hovered over

    private GipfBoardColors() {
    }                                       // Private constructor, makes no sense to use it externally
}
