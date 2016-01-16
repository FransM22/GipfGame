package AI.Players;

import GameLogic.GipfBoardState;
import GameLogic.Move;
import com.sun.javafx.scene.control.behavior.OptionalBoolean;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Created by frans on 2-12-2015.
 */
public abstract class ComputerPlayer<T> implements Function<GipfBoardState, Move> {
    public OptionalInt maxDepth = OptionalInt.empty();
    public Optional<Field> heuristic = Optional.empty();
    public boolean isNondetermenistic = false;

    public Move getMoveWithHighestHeuristicValue(GipfBoardState gipfBoardState, boolean reverseOrder) {
        // Using a treemap instead of a hashmap, because treemaps automatically sort their elements (in this case doubles)
        TreeMap<Double, Move> moveGipfBoardStateMap = new TreeMap<>();
        gipfBoardState.exploreAllChildren();
        for (Map.Entry<Move, GipfBoardState> exploredChildEntry : gipfBoardState.exploredChildren.entrySet()) {

            // Sorts all board states based on the heuristic

            double doubleHeuristicValue;

            try {
                T heuristicValue = (T) heuristic.get().get(exploredChildEntry.getValue().boardStateProperties);
                if (heuristicValue instanceof Double) {
                    doubleHeuristicValue = (Double) heuristicValue;
                }
                else if (heuristicValue instanceof Long) {
                    doubleHeuristicValue = ((Long) heuristicValue).doubleValue();
                }
                else if (heuristicValue instanceof Integer) {
                    doubleHeuristicValue = ((Integer) heuristicValue).doubleValue();
                }
                else {
                    doubleHeuristicValue = 0;   // TODO
                }

                if (isNondetermenistic) {
                    doubleHeuristicValue += Math.random();
                }
                moveGipfBoardStateMap.put(doubleHeuristicValue, exploredChildEntry.getKey());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (moveGipfBoardStateMap.size() >= 1) {
            if (reverseOrder)
                return moveGipfBoardStateMap.lastEntry().getValue();
            else
                return moveGipfBoardStateMap.firstEntry().getValue();
        }
        return null;
    }

    public Move apply(GipfBoardState gipfBoardState) {
        return getMoveWithHighestHeuristicValue(gipfBoardState, false);
    }
}
