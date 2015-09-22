package GUI.GipfBoardComponent;

import GameLogic.Game;
import GameLogic.Position;

import java.awt.*;

import static GUI.GipfBoardComponent.PrimitiveShapeHelper.centerCircleOn;

/**
 * Created by frans on 22-9-2015.
 */
public class GipfPiecePainter {
    private final Color whiteSingleColor = new Color(0xF9F9F9);         // Color of the normal white piece
    private final Color whiteGipfColor = whiteSingleColor;              // Color of the white gipf piece
    private final Color blackSingleColor = new Color(0x525252);         // Color of the normal black piece
    private final Color blackGipfColor = blackSingleColor;              // Color of the black gipf piece
    private final Color singlePieceBorderColor = Color.black;           // Border color of normal single pieces
    private final Color gipfPieceBorderColor = new Color(0xDA0000);     // Border color of gipf pieces
    private final Stroke normalPieceStroke = new BasicStroke(4.0f);
    private final Stroke gipfPieceStroke = new BasicStroke(4.0f);
    private final int pieceSize = 50;                               // The size in pixels in which the pieces are displayed
    private final int hoverCircleSize = 20;
    private final Stroke hoverPositionStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);     // A dashed stroke style. Don't really know how this works.

    private final Color hoverBorderColor = GipfBoardComponent.lineColor;                   // The border color of positions that is hovered over
    private final Color hoverFillColor = GipfBoardComponent.backgroundColor;               // The filling color of positions that is hovered over

    private GipfBoardComponent gipfBoardComponent;

    GipfPiecePainter(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
    }

    void drawPiece(Graphics2D g2, Position position, Game.Piece piece) {
        Color fillColor = null;     // Needs an initial value
        Color borderColor = null;   // Needs an initial value
        Stroke strokeType = normalPieceStroke;

        switch (piece) {
            case WHITE_SINGLE:
                fillColor = whiteSingleColor;
                borderColor = singlePieceBorderColor;
                break;
            case WHITE_GIPF:
                fillColor = whiteGipfColor;
                borderColor = gipfPieceBorderColor;
                strokeType = gipfPieceStroke;
                break;
            case BLACK_SINGLE:
                fillColor = blackSingleColor;
                borderColor = singlePieceBorderColor;
                break;
            case BLACK_GIPF:
                fillColor = blackGipfColor;
                borderColor = gipfPieceBorderColor;
                strokeType = gipfPieceStroke;
                break;
        }

        PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
        PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(position), positionHelper.positionToScreenY(position), pieceSize, fillColor, borderColor, strokeType);
    }

    void paintSelectedPosition(Graphics2D g2, PositionHelper positionHelper) {
        if (gipfBoardComponent.selectedPosition != null) {
            PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(gipfBoardComponent.selectedPosition), positionHelper.positionToScreenY(gipfBoardComponent.selectedPosition), pieceSize, whiteSingleColor, singlePieceBorderColor, hoverPositionStroke);
        }
    }

    void paintHoverCircle(Graphics2D g2, PositionHelper positionHelper) {
        if (gipfBoardComponent.currentHoverPosition != null) {
            PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(gipfBoardComponent.currentHoverPosition), positionHelper.positionToScreenY(gipfBoardComponent.currentHoverPosition), hoverCircleSize, hoverFillColor, hoverBorderColor, hoverPositionStroke);
        }
    }
}
