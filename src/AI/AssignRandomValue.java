package AI;

import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 12-11-2015.
 */
class AssignRandomValue implements Function<GipfBoardState, Double> {
    @Override
    public Double apply(GipfBoardState gipfBoardState) {
        return Math.random();
    }
}
