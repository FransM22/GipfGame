package GUI.GipfBoardComponent;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public class PrimitiveShapeHelper {
    private final Graphics2D g2;

    public PrimitiveShapeHelper(Graphics2D g2) {
        this.g2 = g2;
    }

    public void centerCircleOn(int x, int y, int size, Color fillColor, Color borderColor, Stroke strokeStyle) {
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

    public void centerCircleOn(int x, int y, int size, Color fillColor, Color borderColor) {
        centerCircleOn(x, y, size, fillColor, borderColor, new BasicStroke(1));
    }
}
