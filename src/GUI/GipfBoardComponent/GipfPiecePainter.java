package GUI.GipfBoardComponent;

import GUI.GipfBoardComponent.Definitions.GipfBoardColors;
import GUI.GipfBoardComponent.Definitions.GipfBoardDefinitions;
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
        Stroke strokeType = GipfBoardDefinitions.normalPieceStroke;

        switch (piece) {
            case WHITE_SINGLE:
                fillColor = GipfBoardColors.whiteSingleColor;
                borderColor = GipfBoardColors.singlePieceBorderColor;
                break;
            case WHITE_GIPF:
                fillColor = GipfBoardColors.whiteGipfColor;
                borderColor = GipfBoardColors.gipfPieceBorderColor;
                strokeType = GipfBoardDefinitions.gipfPieceStroke;
                break;
            case BLACK_SINGLE:
                fillColor = GipfBoardColors.blackSingleColor;
                borderColor = GipfBoardColors.singlePieceBorderColor;
                break;
            case BLACK_GIPF:
                fillColor = GipfBoardColors.blackGipfColor;
                borderColor = GipfBoardColors.gipfPieceBorderColor;
                strokeType = GipfBoardDefinitions.gipfPieceStroke;
                break;
        }

        PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
        PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(position), positionHelper.positionToScreenY(position), GipfBoardDefinitions.pieceSize, fillColor, borderColor, strokeType);
    }

    void paintSelectedPosition(Graphics2D g2, PositionHelper positionHelper) {
        if (gipfBoardComponent.selectedPosition != null) {
            PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(gipfBoardComponent.selectedPosition), positionHelper.positionToScreenY(gipfBoardComponent.selectedPosition), GipfBoardDefinitions.pieceSize, GipfBoardColors.whiteSingleColor, GipfBoardColors.singlePieceBorderColor, GipfBoardDefinitions.hoverPositionStroke);
        }
    }

    void paintHoverCircle(Graphics2D g2, PositionHelper positionHelper) {
        if (gipfBoardComponent.currentHoverPosition != null) {
            PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(gipfBoardComponent.currentHoverPosition), positionHelper.positionToScreenY(gipfBoardComponent.currentHoverPosition), GipfBoardDefinitions.hoverCircleSize, GipfBoardColors.hoverFillColor, GipfBoardColors.hoverBorderColor, GipfBoardDefinitions.hoverPositionStroke);
        }
    }
}
