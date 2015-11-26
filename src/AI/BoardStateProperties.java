package AI;

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
    public int mcts_depth;
    private GipfBoardState gipfBoardState;


    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    /**
     * Updates all values for the board state
     */
    public void update() {
        this.heuristicRandomValue = new AssignRandomValue().apply(gipfBoardState);
        this.heuristicWhiteMinusBlack = new AssignWhiteMinusBlack().apply(gipfBoardState);
        this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
        if (mcts_depth > 0) {
            this.mctsDouble = new AssignMCTSValue().apply(gipfBoardState);    // Don't update it again for every move

            gipfBoardState.exploreAllChildren();
            gipfBoardState.exploredChildren.values().stream().forEach(childState -> childState.boardStateProperties.mcts_depth = mcts_depth - 1);
            // TODO this runs way too many times now
            gipfBoardState.exploredChildren.values().parallelStream().forEach(childState -> childState.boardStateProperties.update());
        }
    }


}
