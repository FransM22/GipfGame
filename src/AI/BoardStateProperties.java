package AI;

import AI.Players.MCTSPlayer;
import AI.Players.MinimaxPlayer;
import GameLogic.GipfBoardState;

/**
 * This class stores different values assigned to a board state. (Evaluatged boards have a 1-1 relation with BoardStateProperties
 * objects.
 * <p/>
 * All fields in this class are automatically added to the heuristic selection combo box in GUI2.
 * Created by frans on 12-11-2015.
 */
public class BoardStateProperties {
    public double heuristicRandomValue;
    public int heuristicWhiteMinusBlack;
    public double mctsDouble;
    public int minMaxValue;
    public int mcts_n; // The number of this node
    public int mcts_w; // The number of wins (including the current move)
    public int depth = 0;
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
    }

    /**
     * Updates all values for the board state
     * Should preferably be run in a separate thread
     */
    public void updateChildren() {
        updateBoardState();

        // The maximum depth required. Can be updated if a different algorithm requires a deeper traversal of the tree.
        if (depth <= Math.max(MCTSPlayer.MCTSDepth, MinimaxPlayer.MaxminmaxDepth)) {
            gipfBoardState.exploreAllChildren();
//            gipfBoardState.exploredChildren.values().stream().forEach(childState -> childState.boardStateProperties.depth = depth + 1);
            gipfBoardState.exploredChildren.values().parallelStream().forEach(childState -> childState.boardStateProperties.updateChildren());
        }

        if (depth <= MCTSPlayer.MCTSDepth) {
            this.mctsDouble = new AssignMCTSValue().apply(gipfBoardState);
        }

        if (depth <= MinimaxPlayer.MaxminmaxDepth) {
            this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
        }
    }


}
