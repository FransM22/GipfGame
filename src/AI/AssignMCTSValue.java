package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.util.function.Function;

/**
 * Created by Dingding
 */
public class AssignMCTSValue implements Function<GipfBoardState, Double> {
    private int t = 0; //Total number simulations hence private

    @Override
    public Double apply(GipfBoardState startNodeBoardState) {

        if (startNodeBoardState.boardStateProperties.mcts_depth > 0) {
            int current_w = 0; //wi,  Number wins after current move
            int current_n = 0; //ni, Number plays/simulations after current move
            double c = Math.sqrt(2); // Exploration parameter
            //double MCTSValue = (current_w / current_n) + c * Math.sqrt(current_n / t); // This line gives a division by 0 error

            Game game = new BasicGame();
            game.loadState(startNodeBoardState);

            for (Move move : game.getAllowedMoves()) {
                try {
                    if (!startNodeBoardState.exploredChildren.containsKey(move)) {
                        startNodeBoardState.exploreChild(move);
                    }
                    Game temporaryGame = game.getClass().newInstance();
                    temporaryGame.loadState(startNodeBoardState.exploredChildren.get(move));

                    current_n++;
                    current_w += Math.round(Math.random());

                    temporaryGame.getGipfBoardState().boardStateProperties.mcts_depth = startNodeBoardState.boardStateProperties.mcts_depth - 1;
                    temporaryGame.getGipfBoardState().boardStateProperties.mcts_n = current_n;
                    temporaryGame.getGipfBoardState().boardStateProperties.mcts_w = current_w;

                    temporaryGame.getGipfBoardState().boardStateProperties.mctsDouble = new AssignMCTSValue().apply(temporaryGame.getGipfBoardState());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0.; //MCTSValue;
    }
}



