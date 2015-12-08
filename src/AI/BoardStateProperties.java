package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;

/**
 * This class stores different values assigned to a board state. (Evaluatged boards have a 1-1 relation with BoardStateProperties
 * objects.
 * <p/>
 * All fields in this class are automatically added to the heuristic selection combo box in GUI2.
 * Created by frans on 12-11-2015.
 */
public class BoardStateProperties {
    public static long run_counter = 0;
    public double heuristicRandomValue;
    public int heuristicWhiteMinusBlack;
    public double mctsValue;
    public long ringValue;
    public int minMaxValue;
    public int mcts_n; // The number of this node
    public int mcts_w; // The number of wins (including the current move)
    public int depth = 0;
    public boolean isExploringChildren = false;
    private GipfBoardState gipfBoardState;


    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    public void updateBoardState() {
        if (gipfBoardState.parent != null) {
            this.depth = gipfBoardState.parent.boardStateProperties.depth + 1;
        }
        this.heuristicRandomValue = new AssignRandomValue().apply(gipfBoardState);
        this.heuristicWhiteMinusBlack = new AssignWhiteMinusBlack().apply(gipfBoardState);
        this.ringValue = new AssignRingValue().apply(gipfBoardState);
    }

    /**
     * Updates all values for the board state
     * Should preferably be ran in a separate thread
     */
    public void updateChildren() {
        updateBoardState();
        run_counter++;
        if (run_counter % 100 == 0)
            Thread.yield(); // Don't consume everything on the thread

        // The maximum depth required. Can be updated if a different algorithm requires a deeper traversal of the tree.
        Game game = new BasicGame();
        game.loadState(gipfBoardState);

        if (depth <= 2) {
            gipfBoardState.exploreAllChildren();
            if (!isExploringChildren) {
                isExploringChildren = true;
                new Thread(() -> {
                    gipfBoardState.exploredChildren.values().stream().forEach(childState -> childState.boardStateProperties.updateChildren());
                    isExploringChildren = false;
                }).start();
            }
        }

        if (depth <= 2) {
            this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
        }
    }


}
