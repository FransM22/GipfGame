package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Direction;
import GameLogic.Position;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by frans on 22-9-2015.
 */
public class DrawableGipfBoard extends DrawableObject {
    /*
     * line sets are used for easier drawing of the lines which indicate how the player is allowed to move.
     * Each line set contains a start and endpoint of a line on the board. In addition to each of these two points a direction
     * is stored, in which the next (parallel) line can be found. The last number indicates how many times a parallel
     * line can be drawn.
     * Each set of parallel lines is divided into two, because the direction in which the points move changes halfway
     */
    private final Lines[] lineSets = {
            new Lines(g2, gipfBoardComponent, new Position('a', 2), new Position('f', 1), Direction.NORTH, Direction.NORTH_EAST, 4),
            new Lines(g2, gipfBoardComponent, new Position('b', 6), new Position('i', 2), Direction.NORTH_EAST, Direction.NORTH, 3),
            new Lines(g2, gipfBoardComponent, new Position('d', 1), new Position('i', 2), Direction.NORTH_WEST, Direction.NORTH, 4),
            new Lines(g2, gipfBoardComponent, new Position('a', 2), new Position('h', 6), Direction.NORTH, Direction.NORTH_WEST, 3),
            new Lines(g2, gipfBoardComponent, new Position('b', 1), new Position('b', 6), Direction.SOUTH_EAST, Direction.NORTH_EAST, 4),
            new Lines(g2, gipfBoardComponent, new Position('f', 1), new Position('f', 8), Direction.NORTH_EAST, Direction.SOUTH_EAST, 3)
    };

    public DrawableGipfBoard(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        // Paint the background of the component
        g2.setColor(UIval.get().backgroundColor);
        g2.fillRect(0, 0, gipfBoardComponent.getWidth(), gipfBoardComponent.getHeight());

        g2.setColor(UIval.get().centerColor);
        // Java8 stuff. Basically maps each of the positions in cornerPositions to a x and y value.
        g2.fillPolygon(
                Arrays.stream(UIval.get().centerCornerPositions).mapToInt(positionHelper::positionToScreenX).toArray(),
                Arrays.stream(UIval.get().centerCornerPositions).mapToInt(positionHelper::positionToScreenY).toArray(),
                UIval.get().centerCornerPositions.length
        );

        // Draw the lines
        g2.setColor(UIval.get().lineColor);

        for (Lines lineSet : lineSets) {
            lineSet.draw();
        }
    }
}
