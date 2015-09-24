package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

/**
 * Created by frans on 22-9-2015.
 */
public class HoverCircle extends Circle {
    public HoverCircle(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Set<Position> hoverCirclePositions) {
        super(g2, gipfBoardComponent, hoverCirclePositions, UIval.get().hoverCircleSize, UIval.get().hoverFillColor, UIval.get().hoverBorderColor, UIval.get().hoverPositionStroke);
    }
}
