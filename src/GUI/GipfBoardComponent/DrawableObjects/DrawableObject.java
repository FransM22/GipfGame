package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.PositionHelper;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public abstract class DrawableObject {
    final GipfBoardComponent gipfBoardComponent;
    final PositionHelper positionHelper;
    final Graphics2D g2;

    DrawableObject(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        this.g2 = g2;
        this.gipfBoardComponent = gipfBoardComponent;
        this.positionHelper = new PositionHelper(gipfBoardComponent);
    }

    public abstract void draw();

}
