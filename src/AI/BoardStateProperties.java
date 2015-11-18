package AI;

import GameLogic.GipfBoardState;

/**
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
        this.mctsDouble = Math.random();    // TODO update with real value
        this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
    }
}
