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
    private static long nrOfInstances = 0;

    @Override
    public Double apply(GipfBoardState startNodeBoardState) {

        if (startNodeBoardState.boardStateProperties.mcts_depth > 0) {
            Game game = new BasicGame();
            game.loadState(startNodeBoardState);

            game.getGipfBoardState().exploreAllChildren();
            game.getGipfBoardState().exploredChildren.values().forEach(temporaryBoardState -> {
                try {
                    for (int current_n = 0; current_n <= 3; current_n++) {
                        int w = 0; //wi,  Number wins after current move
                        double c = Math.sqrt(2); // Exploration parameter
                        //double MCTSValue = (current_w / current_n) + c * Math.sqrt(current_n / t); // This line gives a division by 0 error

                        Game temporaryGame = game.getClass().newInstance();
                        temporaryGame.loadState(temporaryBoardState);

                        w += Math.round(Math.random());

                        temporaryGame.getGipfBoardState().boardStateProperties.mcts_depth = startNodeBoardState.boardStateProperties.mcts_depth - 1;
                        temporaryGame.getGipfBoardState().boardStateProperties.mcts_n = current_n;
                        temporaryGame.getGipfBoardState().boardStateProperties.mcts_w += w;
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println("AssignMCTSValue ran for the " + ++nrOfInstances +  "th time");


        return 0.; //MCTSValue;
    }
}



