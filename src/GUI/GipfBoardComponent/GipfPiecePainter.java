package GUI.GipfBoardComponent;

import GameLogic.Game;
import GameLogic.Position;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
class GipfPiecePainter {
    private final GipfBoardComponent gipfBoardComponent;

    GipfPiecePainter(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
    }

    void drawPiece(Graphics2D g2, Position position, Game.Piece piece) {
        Color fillColor = null;     // Needs an initial value
        Color borderColor = null;   // Needs an initial value
        Stroke strokeType = UIval.get().normalPieceStroke;

        switch (piece) {
            case WHITE_SINGLE:
                fillColor = UIval.get().whiteSingleColor;
                borderColor = UIval.get().singlePieceBorderColor;
                break;
            case WHITE_GIPF:
                fillColor = UIval.get().whiteGipfColor;
                borderColor = UIval.get().gipfPieceBorderColor;
                strokeType = UIval.get().gipfPieceStroke;
                break;
            case BLACK_SINGLE:
                fillColor = UIval.get().blackSingleColor;
                borderColor = UIval.get().singlePieceBorderColor;
                break;
            case BLACK_GIPF:
                fillColor = UIval.get().blackGipfColor;
                borderColor = UIval.get().gipfPieceBorderColor;
                strokeType = UIval.get().gipfPieceStroke;
                break;
        }

        PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
        PrimitiveShapeHelper primitiveShapeHelper = new PrimitiveShapeHelper(g2);

        primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(position), positionHelper.positionToScreenY(position), UIval.get().pieceSize, fillColor, borderColor, strokeType);
    }

    void paintSelectedPosition(Graphics2D g2, PositionHelper positionHelper) {
        PrimitiveShapeHelper primitiveShapeHelper = new PrimitiveShapeHelper(g2);

        if (gipfBoardComponent.selectedPosition != null) {
            primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(gipfBoardComponent.selectedPosition), positionHelper.positionToScreenY(gipfBoardComponent.selectedPosition), UIval.get().pieceSize, UIval.get().whiteSingleColor, UIval.get().singlePieceBorderColor, UIval.get().hoverPositionStroke);
        }
    }

    void paintHoverCircle(Graphics2D g2, PositionHelper positionHelper) {
        PrimitiveShapeHelper primitiveShapeHelper = new PrimitiveShapeHelper(g2);

        if (gipfBoardComponent.currentHoverPosition != null) {
            primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(gipfBoardComponent.currentHoverPosition), positionHelper.positionToScreenY(gipfBoardComponent.currentHoverPosition), UIval.get().hoverCircleSize, UIval.get().hoverFillColor, UIval.get().hoverBorderColor, UIval.get().hoverPositionStroke);
        }
    }
}
