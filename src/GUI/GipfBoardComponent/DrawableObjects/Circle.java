package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

/**
 * Created by frans on 24-9-2015.
 */
public class Circle extends DrawableObject {
    private Set<Position> circlePositions;
    private int size;
    private Color fillColor;
    private Color borderColor;
    private Stroke strokeStyle;

    Circle(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Set<Position> circlePositions, int size, Color fillColor, Color borderColor, Stroke strokeStyle) {
        super(g2, gipfBoardComponent);
        this.circlePositions = circlePositions;
        this.size = size;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.strokeStyle = strokeStyle;
    }

    @Override
    public void draw() {
        circlePositions.stream().filter(position -> position != null).forEach(this::drawCircle);
    }

    private void drawCircle(Position position) {
        int x = positionHelper.positionToScreenX(position);
        int y = positionHelper.positionToScreenY(position);

        g2.setColor(fillColor);
        g2.fillOval(
                x - (size / 2),
                y - (size / 2),
                size,
                size
        );

        g2.setStroke(strokeStyle);
        g2.setColor(borderColor);
        g2.drawOval(
                x - (size / 2),
                y - (size / 2),
                size,
                size
        );
    }
}
