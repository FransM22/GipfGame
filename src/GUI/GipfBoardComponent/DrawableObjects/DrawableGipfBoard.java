package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Move;
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
    private final LineSet[] lineSets = {
            new LineSet(new Position('a', 2), new Position('f', 1), Move.Direction.NORTH, Move.Direction.NORTH_EAST, 4),
            new LineSet(new Position('b', 6), new Position('i', 2), Move.Direction.NORTH_EAST, Move.Direction.NORTH, 3),
            new LineSet(new Position('d', 1), new Position('i', 2), Move.Direction.NORTH_WEST, Move.Direction.NORTH, 4),
            new LineSet(new Position('a', 2), new Position('h', 6), Move.Direction.NORTH, Move.Direction.NORTH_WEST, 3),
            new LineSet(new Position('b', 1), new Position('b', 6), Move.Direction.SOUTH_EAST, Move.Direction.NORTH_EAST, 4),
            new LineSet(new Position('f', 1), new Position('f', 8), Move.Direction.NORTH_EAST, Move.Direction.SOUTH_EAST, 3)
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

        for (LineSet lineSet : lineSets) {
            int startDeltaPos = gipfBoardComponent.game.getDeltaPos(lineSet.nextStart);
            int endDeltaPos = gipfBoardComponent.game.getDeltaPos(lineSet.nextEnd);

            for (int lineNr = 0; lineNr < lineSet.nr; lineNr++) {
                Position start = new Position(lineSet.start.getPosId() + lineNr * startDeltaPos);
                Position end = new Position(lineSet.end.getPosId() + lineNr * endDeltaPos);

                g2.drawLine(
                        positionHelper.positionToScreenX(start),
                        positionHelper.positionToScreenY(start),
                        positionHelper.positionToScreenX(end),
                        positionHelper.positionToScreenY(end)
                );
            }
        }
    }

    private class LineSet {
        final Position start;
        final Position end;
        final Move.Direction nextStart;
        final Move.Direction nextEnd;
        final int nr;

        public LineSet(Position start, Position end, Move.Direction nextStart, Move.Direction nextEnd, int nr) {
            this.start = start;
            this.end = end;
            this.nextStart = nextStart;
            this.nextEnd = nextEnd;
            this.nr = nr;
        }
    }
}
