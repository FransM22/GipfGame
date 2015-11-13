package AI;

import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 13-11-2015.
 */
public class AssignMinMaxValue implements Function<GipfBoardState, Integer> {
    @Override
    public Integer apply(GipfBoardState gipfBoardState) {
        if (gipfBoardState.players.winner() == null) return 0;
        if (gipfBoardState.players.winner() == gipfBoardState.players.white) return 100;
        return -100;
    }
}
