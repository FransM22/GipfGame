package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Position;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by frans on 22-9-2015.
 */
public class SelectedPosition extends Circle {
    public SelectedPosition(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Position selectedPosition) {
        super(g2, gipfBoardComponent, new HashSet<>(Arrays.asList(selectedPosition)), UIval.get().pieceSize, UIval.get().whiteSingleColor, UIval.get().singlePieceBorderColor, UIval.get().hoverPositionStroke);
    }
}
