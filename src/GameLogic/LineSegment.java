package GameLogic;

import GameLogic.Game.Game;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 6-10-2015.
 */
public class LineSegment extends Line {
    Position startPosition;
    Position endPosition;

    public LineSegment(Game game, Position endpoint1, Position endpoint2, Direction direction) {
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
        if (!(o instanceof LineSegment)) return false;
        if (!super.equals(o)) return false;

        LineSegment positions = (LineSegment) o;

        if (!startPosition.equals(positions.startPosition)) return false;
        return endPosition.equals(positions.endPosition);

    }

    public boolean intersectsWith(LineSegment lineSegment) {
        for (Position position : getAllPositions()) {
            for (Position otherPosition : lineSegment.getAllPositions()) {
                if (position.equals(otherPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Position> getOccupiedPositions(GipfBoardState gipfBoardState) {
        return getAllPositions()
                .stream()
                .filter(position -> gipfBoardState.getPieceMap()
                        .containsKey(position))
                .collect(toSet());
    }

    public Set<Position> getAllPositions() {
        return super.getPositions()
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
        return "LineSegment{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
