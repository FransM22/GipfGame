package AI;

import GameLogic.GipfBoardState;

/**
 * This class stores different values assigned to a board state. (Evaluatged boards have a 1-1 relation with BoardStateProperties
 * objects.
 *
 * All fields in this class are automatically added to the heuristic selection combo box in GUI2.
 * Created by frans on 12-11-2015.
 */
public class BoardStateProperties {
    private GipfBoardState gipfBoardState;
    public double heuristicRandomValue;
    public int heuristicWhiteMinusBlack;
    public double mctsDouble;
    public int minMaxValue;

    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    /**
     * Updates all values for the board state
     */
    public void update() {
        this.heuristicRandomValue = new AssignRandomValue().apply(gipfBoardState);
        this.heuristicWhiteMinusBlack = new AssignWhiteMinusBlack().apply(gipfBoardState);
        this.mctsDouble = new AssignMCTSValue().apply(gipfBoardState);
        this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
    }
}
