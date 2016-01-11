package AI;

import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 11-1-2016.
 */
public class AssignWeightedHeuristicValue implements Function<GipfBoardState, Double> {
    @Override
    public Double apply(GipfBoardState gipfBoardState) {
        return 0.5 * gipfBoardState.boardStateProperties.blobValue + 0.5 * gipfBoardState.boardStateProperties.longValue;
    }
}
