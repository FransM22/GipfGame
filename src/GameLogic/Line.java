package GameLogic;

import GameLogic.Game.Game;

import java.util.HashSet;
import java.util.Set;

/**
 * A line is uniquely defined by a startPosition and a direction. A Line can be created by just any position and a direction,
 * internally it is represented by the position with the position with the lowest id value and the corresponding direction.
 * <p/>
 * Created by frans on 5-10-2015.
 */
public class Line {
    Game game;
    Position startPosition;
    Direction direction;

    public Line(Game game, Position position, Direction direction) {
        int deltaPos = direction.getDeltaPos();

        if (deltaPos > 0) {
            deltaPos = -deltaPos;
        }

        Position currentPosition = position;
        Position nextPosition = new Position(currentPosition.getPosId() + deltaPos);
        for (; game.isPositionOnBigBoard(nextPosition); nextPosition = new Position(nextPosition.getPosId() + deltaPos)) {
            currentPosition = nextPosition;
        }

        this.startPosition = currentPosition;
        this.direction = Direction.getDirectionFromDeltaPos(-deltaPos);
        this.game = game;
    }

    public boolean intersectsWith(Line other) {
        Position currentPositionThisLine = new Position(startPosition);
        Position currentPositionOtherLine = new Position(other.startPosition);

        while (game.isPositionOnBigBoard(currentPositionThisLine)) {
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

    public Set<Position> getPositions() {
        Set<Position> positions = new HashSet<>();

        Position currentPosition = new Position(this.startPosition);
        for (; game.isPositionOnBigBoard(currentPosition); currentPosition = new Position(currentPosition.posId + direction.getDeltaPos())) {
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
}
