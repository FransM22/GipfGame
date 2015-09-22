package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Position;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public class FilledCircles extends DrawableObject {
    public FilledCircles(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        for (Position position : UIval.get().filledCirclePositions) {
            primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(position), positionHelper.positionToScreenY(position), UIval.get().filledCircleSize, UIval.get().filledCircleColor, UIval.get().filledCircleBorderColor);
        }
    }
}
