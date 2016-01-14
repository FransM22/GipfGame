package AI;

import GameLogic.GipfBoardState;
import GameLogic.PieceColor;
import GameLogic.Position;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 7-1-2016.
 */
public class AssignLongValue implements Function<GipfBoardState, Integer> {
    @Override
    public Integer apply(GipfBoardState gipfBoardState) {
        int value = 0;

        for (Position p : getDiagonalPositions()) {
            if (gipfBoardState.getPieceMap().containsKey(p)) {
                if (gipfBoardState.getPieceMap().get(p).getPieceColor() == PieceColor.WHITE) {
                    value -= 1;
                } else value += 1;
            }
        }

        return value;
    }

    private Set<Position> getDiagonalPositions() {
        return Arrays.asList(
                new Position('e', 1),
                new Position('e', 2),
                new Position('e', 3),
                new Position('e', 4),
                new Position('e', 5),
                new Position('e', 6),
                new Position('e', 7),
                new Position('e', 8),
                new Position('e', 9),

                new Position('a', 1),
                new Position('b', 2),
                new Position('c', 3),
                new Position('d', 4),
                new Position('f', 6),
                new Position('g', 7),
                new Position('h', 8),
                new Position('i', 9),

                new Position('a', 9),
                new Position('b', 8),
                new Position('c', 7),
                new Position('d', 6),
                new Position('f', 4),
                new Position('g', 3),
                new Position('h', 2),
                new Position('i', 1)).stream().collect(toSet());
    }
}
