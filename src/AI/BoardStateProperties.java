package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;

/**
 * This class stores different values assigned to a board state. (Evaluatged boards have a 1-1 relation with BoardStateProperties
 * objects.
 * <p>
 * All fields in this class are automatically added to the heuristic selection combo box in GUI2.
 * Created by frans on 12-11-2015.
 */
public class BoardStateProperties {
    public double heuristicRandomValue;
    public long heuristicBlackMinusWhite;
    public double mctsValue;
    public long ringValue;
    public long blobValue;
    public double heuristicMin;
    public double heuristicMax;
    public double minMaxValue;
    public int mcts_n; // The number of this node
    public int mcts_w; // The number of wins (including the current move)
    public long depth = 0;
    public long longValue;
    public boolean isExploringChildren = false;
    public double weightedHeuristic;
    private GipfBoardState gipfBoardState;


    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    public void updateBoardState() {
        if (gipfBoardState.parent != null) {
            this.depth = gipfBoardState.parent.boardStateProperties.depth + 1;
        }
        this.heuristicRandomValue = new AssignRandomValue().apply(gipfBoardState);
        this.heuristicBlackMinusWhite = new AssignBlackMinusWhite().apply(gipfBoardState);
        this.ringValue = new AssignRingValue().apply(gipfBoardState);
        this.blobValue = new AssignBlobValue().apply(gipfBoardState);
        this.longValue = new AssignLongValue().apply(gipfBoardState);
        this.weightedHeuristic = new AssignWeightedHeuristicValue().apply(gipfBoardState);
        // The mcts value is updated in a separate thread
    }

    /**
     * Updates all values for the board state
     * Should preferably be ran in a separate thread
     */
    public void updateChildren() {
        updateBoardState();

        // The maximum depth required. Can be updated if a different algorithm requires a deeper traversal of the tree.
        Game game = new BasicGame();
        game.loadState(gipfBoardState);

        if (depth <= 0) {
            gipfBoardState.exploreAllChildren();
            if (!isExploringChildren) {
                isExploringChildren = true;

                gipfBoardState.exploredChildren.values().stream().forEach(childState -> childState.boardStateProperties.updateChildren());
                isExploringChildren = false;
            }
        }

        if (depth <= 2) {
            this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
        }
    }
}
