package AI;

import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 18-11-2015.
 */
public class AssignMCTSValue implements Function<GipfBoardState, Double> {
    @Override
    public Double apply(GipfBoardState gipfBoardState) {
        return Math.random();    // TODO update with real value
    }
}
