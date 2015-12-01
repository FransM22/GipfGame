package AI.Players;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.util.TreeMap;
import java.util.function.Function;

/**
 * Created by Dingding.
 */
public class MCTSPlayer implements Function<GipfBoardState, Move> {
    public static int MCTSDepth = 2;

    /* 
     TODO: Update BoardStaeProperties class with heuristic code
     */
    boolean gameOver = false; //TESTING PURPOSE

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        int depth = 5; //Depth until MCTS has to select before going random
        int tempDepth = 0; //Starting depth MCTS

        Game game = new BasicGame();
        game.loadState(gipfBoardState);

        //Stage 1, repeat until reach depth
        // Using a treemap instead of a hashmap, because treemaps automatically sort their elements (in this case doubles)
        while(tempDepth < depth)
        {
            TreeMap<Double, Move> moveGipfBoardStateMap = new TreeMap<>();
            for (Move move : game.getAllowedMoves()) {
                Game temporaryGame = new BasicGame();
                temporaryGame.loadState(gipfBoardState);
                temporaryGame.applyMove(move);

                // Sorts all board states based on mctsDouble
                //TO DO Has to pick highest value, not lowest
                //moveGipfBoardStateMap.put(temporaryGame.getGipfBoardState().boardStateProperties.mctsDouble, move);
            }

            if (moveGipfBoardStateMap.size() >= 1) {
                return moveGipfBoardStateMap.firstEntry().getValue();
            }
            tempDepth++;
        }
        //Stage 2 & 3, check if game over
        while(!gameOver) //TESTING PURPOSE
        {
            //Generate children and start random movement
            TreeMap<Double, Move> moveGipfBoardStateMap = new TreeMap<>();
            for (Move move : game.getAllowedMoves()) {
                Game temporaryGame = new BasicGame();
                temporaryGame.loadState(gipfBoardState);
                temporaryGame.applyMove(move);

                moveGipfBoardStateMap.put(temporaryGame.getGipfBoardState().boardStateProperties.heuristicRandomValue, move);
            }
            if (moveGipfBoardStateMap.size() >= 1) {
                return moveGipfBoardStateMap.firstEntry().getValue();
            }
        }
        //Stage 4, backpropagation

        //TO DO I am not sure if this is the end return, or if it is the else of the previous if?
        //I will treat it as the end statement
        return null;
    }
}
