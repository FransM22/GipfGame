package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;
import GameLogic.Game;
import GameLogic.Position;

import java.awt.*;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 22-9-2015.
 */
public class GipfPieces extends Circle {

    public GipfPieces(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent, UIval.get().pieceSize, UIval.get().pieceStroke);
        Map<Position, Game.Piece> pieceMap = gipfBoardComponent.game.getGipfBoardState().getPieceMap();

        super.setDrawableCircles(
                pieceMap
                        .entrySet()
                        .stream()
                        .map(entry -> new CircleProperties(
                                (Position) entry.getKey(),
                                getFillColorFor(entry.getValue()),
                                getBorderColorFor(entry.getValue())))
                        .collect(toSet())
        );
    }

    private Color getFillColorFor(Game.Piece piece) {
        switch (Game.getPieceColor(piece)) {
            case WHITE:
                return UIval.get().whitePieceColor;
            case BLACK:
                return UIval.get().blackPieceColor;
        }

        return null;
    }

    private Color getBorderColorFor(Game.Piece piece) {
        switch (piece) {
            case WHITE_SINGLE:
                return UIval.get().singlePieceBorderColor;
            case WHITE_GIPF:
                return UIval.get().gipfPieceBorderColor;
            case BLACK_SINGLE:
                return UIval.get().singlePieceBorderColor;
            case BLACK_GIPF:
                return UIval.get().gipfPieceBorderColor;
        }

        return Color.BLACK;
    }
}
