package GameLogic;

import GameLogic.Game.Game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * A line is uniquely defined by a startPosition and a direction. A Line can be created by just any position and a direction,
 * internally it is represented by the position with the position with the lowest id value and the corresponding direction.
 * <p/>
 * Created by frans on 5-10-2015.
 */
public class Line implements Iterable<Position> {
    private final Game game;
    private final Position startPosition;
    private final Direction direction;

    public Line(Game game, Position position, Direction direction) {
        int deltaPos = direction.getDeltaPos();

        if (deltaPos > 0) {
            deltaPos = -deltaPos;
        }

        Position currentPosition = position;
        Position nextPosition = new Position(currentPosition.getPosId() + deltaPos);
        for (; game.isPositionOnPlayAreaOrOuterDots(nextPosition); nextPosition = new Position(nextPosition.getPosId() + deltaPos)) {
            currentPosition = nextPosition;
        }

        this.startPosition = currentPosition;
        this.direction = Direction.getDirectionFromDeltaPos(-deltaPos);
        this.game = game;
    }

    public boolean intersectsWith(Line other) {
        Position currentPositionThisLine = new Position(startPosition);
        Position currentPositionOtherLine = new Position(other.startPosition);

        while (game.isPositionOnPlayAreaOrOuterDots(currentPositionThisLine)) {
            if (currentPositionThisLine.getPosId() == currentPositionOtherLine.getPosId()) {
                return true;
            } else if (currentPositionThisLine.getPosId() < currentPositionOtherLine.getPosId()) {
                currentPositionThisLine = new Position(currentPositionThisLine.posId + this.direction.getDeltaPos());
            } else if (currentPositionThisLine.getPosId() > currentPositionOtherLine.getPosId()) {
                currentPositionOtherLine = new Position(currentPositionOtherLine.posId + other.direction.getDeltaPos());
            }
        }

        return false;
    }

    public Set<Position> getPositionsOnLine() {
        Set<Position> positions = new HashSet<>();

        Position currentPosition = new Position(this.startPosition);
        for (; game.isPositionOnPlayAreaOrOuterDots(currentPosition); currentPosition = new Position(currentPosition.posId + direction.getDeltaPos())) {
            positions.add(currentPosition);
        }

        return positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;

        Line line = (Line) o;

        if (!startPosition.equals(line.startPosition)) return false;
        return direction == line.direction;

    }

    @Override
    public String toString() {
        return "Line{" +
                "from: " + startPosition +
                " direction: " + direction +
                '}';
    }

    @Override
    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    @Override
    public Iterator<Position> iterator() {
        return getPositionsOnLine().iterator();
    }

    public Position getStartPosition() { return startPosition; }

    public Direction getDirection() { return direction; }

    public static Set<Line> getLinesOnTheBoard(Game game) {
        Set<Line> linesOnTheBoard = new HashSet<>();
        linesOnTheBoard.add(new Line(game, new Position('a', 1), Direction.NORTH_EAST));
        linesOnTheBoard.add(new Line(game, new Position('a', 2), Direction.NORTH_EAST));
        linesOnTheBoard.add(new Line(game, new Position('a', 3), Direction.NORTH_EAST));
        linesOnTheBoard.add(new Line(game, new Position('a', 4), Direction.NORTH_EAST));
        linesOnTheBoard.add(new Line(game, new Position('i', 4), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('i', 3), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('i', 2), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('i', 1), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('h', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('h', 1), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('g', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('g', 1), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('f', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('f', 1), Direction.NORTH_WEST));
        linesOnTheBoard.add(new Line(game, new Position('e', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('d', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('d', 1), Direction.NORTH_EAST));
        linesOnTheBoard.add(new Line(game, new Position('c', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('c', 1), Direction.NORTH_EAST));
        linesOnTheBoard.add(new Line(game, new Position('b', 1), Direction.NORTH));
        linesOnTheBoard.add(new Line(game, new Position('b', 1), Direction.NORTH_EAST));

        return linesOnTheBoard;
    }

    /**
     * Created by frans on 6-10-2015.
     */
    public static class Segment extends Line {
        final Position startPosition;
        final Position endPosition;

        public Segment(Game game, Position endpoint1, Position endpoint2, Direction direction) {
            super(game, endpoint1, direction);

            if (endpoint1.getPosId() < endpoint2.getPosId()) {
                startPosition = endpoint1;
                endPosition = endpoint2;
            }
            else {
                startPosition = endpoint2;
                endPosition = endpoint1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Segment)) return false;
            if (!super.equals(o)) return false;

            Segment positions = (Segment) o;

            if (!startPosition.equals(positions.startPosition)) return false;
            return endPosition.equals(positions.endPosition);

        }

        public boolean intersectsWith(Segment segment) {
            for (Position position : getAllPositions()) {
                for (Position otherPosition : segment.getAllPositions()) {
                    if (position.equals(otherPosition)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Set<Position> getOccupiedPositions(Map<Position, Piece> pieceMap) {
            return getAllPositions()
                    .stream()
                    .filter(position -> pieceMap.containsKey(position))
                    .collect(toSet());
        }

        public Set<Position> getAllPositions() {
            return super.getPositionsOnLine()
                    .stream()
                    .filter(
                            position -> startPosition.posId <= position.posId && position.posId <= endPosition.posId)
                    .collect(toSet());
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + startPosition.hashCode();
            result = 31 * result + endPosition.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "startPosition=" + startPosition +
                    ", endPosition=" + endPosition +
                    '}';
        }
    }
}
