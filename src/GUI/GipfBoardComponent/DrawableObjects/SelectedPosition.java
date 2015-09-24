package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

/**
 * Created by frans on 22-9-2015.
 */
public class SelectedPosition extends Circle {
    public SelectedPosition(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Set<Position> selectedPositions) {
        super(g2, gipfBoardComponent, selectedPositions, UIval.get().pieceSize, UIval.get().whiteSingleColor, UIval.get().singlePieceBorderColor, UIval.get().hoverPositionStroke);
    }
}
