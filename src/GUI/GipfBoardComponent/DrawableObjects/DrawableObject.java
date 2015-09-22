package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.PositionHelper;
import GUI.GipfBoardComponent.PrimitiveShapeHelper;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public abstract class DrawableObject {
    protected GipfBoardComponent gipfBoardComponent;
    protected PositionHelper positionHelper;
    protected PrimitiveShapeHelper primitiveShapeHelper;
    protected Graphics2D g2;

    public DrawableObject(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        this.g2 = g2;
        this.gipfBoardComponent = gipfBoardComponent;
        this.positionHelper = new PositionHelper(gipfBoardComponent);
        this.primitiveShapeHelper = new PrimitiveShapeHelper(g2);
    }

    public abstract void draw();
}
