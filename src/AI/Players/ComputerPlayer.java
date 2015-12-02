package AI.Players;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Created by frans on 2-12-2015.
 */
public abstract class ComputerPlayer<T> implements Function<GipfBoardState, Move> {
    public Optional<Integer> maxDepth = Optional.empty();
    public Optional<Field> heuristic = Optional.empty();

    public Move getMoveWithHighestHeuristicValue(GipfBoardState gipfBoardState, boolean reverseOrder) {
        Game game = new BasicGame();
        game.loadState(gipfBoardState);

        // Using a treemap instead of a hashmap, because treemaps automatically sort their elements (in this case doubles)
        TreeMap<T, Move> moveGipfBoardStateMap = new TreeMap<>();
        for (Move move : game.getAllowedMoves()) {
            Game temporaryGame = new BasicGame();
            temporaryGame.loadState(gipfBoardState);
            temporaryGame.applyMove(move);

            // Sorts all board states based on heuristicRandomValue

            try {
                T heuristicValue = (T) heuristic.get().get(temporaryGame.getGipfBoardState().boardStateProperties);
                moveGipfBoardStateMap.put(heuristicValue, move);
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
