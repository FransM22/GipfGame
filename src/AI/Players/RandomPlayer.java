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
public class RandomPlayer implements Function<GipfBoardState, Move> {

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        Game game = new BasicGame();
        game.loadState(gipfBoardState);


        TreeMap<Double, Move> moveGipfBoardStateMap = new TreeMap<>();
        for (Move move : game.getAllowedMoves()) {
            Game temporaryGame = new BasicGame();
            temporaryGame.loadState(gipfBoardState);
            temporaryGame.applyMove(move);

            moveGipfBoardStateMap.put(temporaryGame.getGipfBoardState().boardStateProperties.heuristicRandomValue, move);
        }

        return moveGipfBoardStateMap.firstEntry().getValue();
    }
}
