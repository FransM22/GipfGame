package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by Dingding
 */
public class AssignMCTSValue implements Function<GipfBoardState, Double> {
    private int t = 0; //Total number simulations hence private

    @Override
    public Double apply(GipfBoardState gipfBoardState) {
        int w = 0; //wi,  Number wins after current move
        int n = 0; //ni, Number plays/simulations after current move
        double c = Math.sqrt(2); // Exploration parameter
        // double MCTSValue = (w / n) + c * Math.sqrt(n / t); // This line gives a division by 0 error

        for (int number_simulations_left = 100; number_simulations_left > 0; number_simulations_left--) {
            // Create a temporary game
            Game game = new BasicGame();
            game.loadState(gipfBoardState);

            // From here a move can be applied
            // any move from
            // game.getAllowedMoves();
            // is allowed
        }
        return 0.; //MCTSValue;
    }
}



