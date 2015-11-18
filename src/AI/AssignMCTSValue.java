package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 18-11-2015.
 */
public class AssignMCTSValue implements Function<GipfBoardState, Double> {
    @Override
    public Double apply(GipfBoardState gipfBoardState) {
        double var4 = 0;

        for (int number_simulations_left = 100; number_simulations_left > 0; number_simulations_left--) {
            // Create a temporary game
            Game game = new BasicGame();
            game.loadState(gipfBoardState);

            // From here a move can be applied
            // any move from
            // game.getAllowedMoves();
            // is allowed
        }


        return  var4;    // TODO update with real value
    }
}
