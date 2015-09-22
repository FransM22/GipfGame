package GUI.GipfBoardComponent;

import java.awt.*;

/**
 * Class is used to have a single overview where all the colors are defined
 * <p/>
 * Created by frans on 22-9-2015.
 */
public final class GipfBoardColors {
    final static Color backgroundColor = new Color(0xD2FF9B);          // The background of the component
    final static Color lineColor = new Color(0x8D8473);                // The lines showing how pieces are allowed to move
    final static Color positionNameColor = lineColor;                  // Color of position names
    final static Color centerColor = new Color(0xE5FFCE);              // The hexagon in the center
    final static Color filledCircleColor = backgroundColor;            // Color of the circles that are filled (on the edges of the board)
    final static Color filledCircleBorderColor = new Color(0x7D8972);  // Border color of the filled circles
    final static Color moveToArrowColor = lineColor;                   // The line indicating where the player can move his piece
    final static Color whiteSingleColor = new Color(0xF9F9F9);         // Color of the normal white piece
    final static Color whiteGipfColor = whiteSingleColor;              // Color of the white gipf piece
    final static Color blackSingleColor = new Color(0x525252);         // Color of the normal black piece
    final static Color blackGipfColor = blackSingleColor;              // Color of the black gipf piece
    final static Color singlePieceBorderColor = Color.black;           // Border color of normal single pieces
    final static Color gipfPieceBorderColor = new Color(0xDA0000);     // Border color of gipf pieces

    private GipfBoardColors() {
    }                                       // Private constructor, makes no sense to use it externally
}
