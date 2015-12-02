package AI.Players;

import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by frans on 2-12-2015.
 */
public abstract class ComputerPlayer implements Function<GipfBoardState, Move> {
    public Optional<Integer> maxDepth = Optional.empty();
    Optional<Field> heuristic = Optional.empty();
}
