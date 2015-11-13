package AI;

import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 13-11-2015.
 */
class AssignWhiteMinusBlack implements Function<GipfBoardState, Integer> {
    @Override
    public Integer apply(GipfBoardState gipfBoardState) {
        return gipfBoardState.players.white.reserve - gipfBoardState.players.black.reserve;
    }
}
