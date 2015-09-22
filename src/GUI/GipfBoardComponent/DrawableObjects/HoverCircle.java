package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public class HoverCircle extends DrawableObject {
    public HoverCircle(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        if (gipfBoardComponent.currentHoverPosition != null) {
            primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(gipfBoardComponent.currentHoverPosition), positionHelper.positionToScreenY(gipfBoardComponent.currentHoverPosition), UIval.get().hoverCircleSize, UIval.get().hoverFillColor, UIval.get().hoverBorderColor, UIval.get().hoverPositionStroke);
        }
    }
}
