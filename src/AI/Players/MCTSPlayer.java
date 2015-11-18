package AI.Players;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.util.TreeMap;
import java.util.function.Function;

/**
 * Created by frans on 13-11-2015.
 */
public class MCTSPlayer implements Function<GipfBoardState, Move> {

    /*
     TODO: Update BoardStaeProperties class with heuristic code
     */
    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        Game game = new BasicGame();
        game.loadState(gipfBoardState);


        // Using a treemap instead of a hashmap, because treemaps automatically sort their elements (in this case doubles)
        TreeMap<Double, Move> moveGipfBoardStateMap = new TreeMap<>();
        for (Move move : game.getAllowedMoves()) {
            Game temporaryGame = new BasicGame();
            temporaryGame.loadState(gipfBoardState);
            temporaryGame.applyMove(move);

            // Sorts all board states based on heuristicRandomValue
            moveGipfBoardStateMap.put(temporaryGame.getGipfBoardState().boardStateProperties.heuristicRandomValue, move);
        }

        if (moveGipfBoardStateMap.size() >= 1) {
            return moveGipfBoardStateMap.firstEntry().getValue();
        }
        return null;
    }
}
