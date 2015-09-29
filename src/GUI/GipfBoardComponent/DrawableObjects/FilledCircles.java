package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

/**
 * Created by frans on 22-9-2015.
 */
public class FilledCircles extends Circle {
    public FilledCircles(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Set<Position> circlePositions) {
        super(g2, gipfBoardComponent, circlePositions, UIval.get().filledCircleSize, UIval.get().filledCircleColor, UIval.get().filledCircleBorderColor, new BasicStroke());
    }
}
