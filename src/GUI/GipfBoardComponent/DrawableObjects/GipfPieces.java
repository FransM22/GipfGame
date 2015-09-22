package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.PositionHelper;
import GUI.GipfBoardComponent.PrimitiveShapeHelper;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Game;
import GameLogic.Position;

import java.awt.*;
import java.util.Map;

/**
 * Created by frans on 22-9-2015.
 */
public class GipfPieces extends DrawableObject {
    private Map<Position, Game.Piece> pieceMap;

    public GipfPieces(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
        pieceMap = gipfBoardComponent.game.getGipfBoard().getPieceMap();
    }

    public void paintHoverCircle(Graphics2D g2, PositionHelper positionHelper) {
        PrimitiveShapeHelper primitiveShapeHelper = new PrimitiveShapeHelper(g2);
    }

    @Override
    public void draw() {
        Color fillColor = null;     // Needs an initial value
        Color borderColor = null;   // Needs an initial value
        Stroke strokeType = UIval.get().normalPieceStroke;

        for (Map.Entry<Position, Game.Piece> entry : pieceMap.entrySet()) {
            Position position = entry.getKey();
            Game.Piece piece = entry.getValue();

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

            primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(position), positionHelper.positionToScreenY(position), UIval.get().pieceSize, fillColor, borderColor, strokeType);
        }
    }
}
