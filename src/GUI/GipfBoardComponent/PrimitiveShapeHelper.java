package GUI.GipfBoardComponent;

import GameLogic.Position;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public class PrimitiveShapeHelper {
    static void centerCircleOn(Graphics2D g2, int x, int y, int size, Color fillColor, Color borderColor, Stroke strokeStyle) {
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

    static void centerCircleOn(Graphics2D g2, int x, int y, int size, Color fillColor, Color borderColor) {
        centerCircleOn(g2, x, y, size, fillColor, borderColor, new BasicStroke(1));
    }
}
