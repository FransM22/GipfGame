package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 24-9-2015.
 */
public class Circle extends DrawableObject {
    private Set<CircleProperties> circles;
    private final int size;
    private final Stroke strokeStyle;

    Circle(Graphics2D g2, GipfBoardComponent gipfBoardComponent, int size, Stroke strokeStyle) {
        super(g2, gipfBoardComponent);
        this.size = size;
        this.strokeStyle = strokeStyle;
    }

    /**
     * In java it's not possible to execute code before the call to the super constructor. This would mean that this class
     * would need to take care of creating the correct color game piece for the GipfPieces class. Because I want to keep
     * the method more general I created a method to set the CircleProperties field, allowing the GipfPieces class to take
     * care of it.
     * @param circles
     */
    void setDrawableCircles(Set<CircleProperties> circles) {
        this.circles = circles;
    }

    Circle(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Set<Position> circlePositions, int size, Color fillColor, Color borderColor, Stroke strokeStyle) {
        super(g2, gipfBoardComponent);
        this.circles = circlePositions
                .stream()
                .filter(position -> position != null)
                .map(position -> new CircleProperties(position, fillColor, borderColor))
                .collect(toSet());
        this.size = size;
        this.strokeStyle = strokeStyle;
    }


    @Override
    public void draw() {
        circles
                .stream()
                .filter(circle -> circle != null)
                .forEach(drawableCircle -> drawCircle(drawableCircle.position, drawableCircle.fillColor, drawableCircle.borderColor));
    }

    private void drawCircle(Position position, Color fillColor, Color borderColor) {
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

    class CircleProperties {
        final Color fillColor;
        final Color borderColor;
        final Position position;

        public CircleProperties(Position position, Color fillColor, Color borderColor) {
            this.position = position;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
        }
    }
}
