package AI;

import AI.Players.MCTSPlayer;
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
    public int depth;
    private GipfBoardState gipfBoardState;


    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    public void updateBoardState() {
        this.heuristicRandomValue = new AssignRandomValue().apply(gipfBoardState);
        this.heuristicWhiteMinusBlack = new AssignWhiteMinusBlack().apply(gipfBoardState);
        this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
    }

    /**
     * Updates all values for the board state
     */
    public void updateChildren() {
        updateBoardState();

        // The maximum depth required
        if (depth <= MCTSPlayer.MCTSDepth) {
            gipfBoardState.exploreAllChildren();
            gipfBoardState.exploredChildren.values().stream().forEach(childState -> childState.boardStateProperties.depth = depth + 1);
            gipfBoardState.exploredChildren.values().parallelStream().forEach(childState -> childState.boardStateProperties.updateChildren());
        }

        if (depth == MCTSPlayer.MCTSDepth) {
            // Only calculate the mcts value for the nodes at the given depth (the cost of also calculating their parents is negligible,
            // but this looks neater
            this.mctsDouble = new AssignMCTSValue().apply(gipfBoardState);
        }
    }


}
