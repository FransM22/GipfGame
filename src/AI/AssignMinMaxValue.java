package AI;

import GameLogic.Game.BasicGame;
import GameLogic.GipfBoardState;

import java.util.function.Function;

/**
 * Created by frans on 13-11-2015.
 */
class AssignMinMaxValue implements Function<GipfBoardState, Integer> {

    @Override
    public Integer apply(GipfBoardState gipfBoardState) {
        BasicGame game = new BasicGame();
        game.loadState(gipfBoardState);

        if (game.getGipfBoardState().players.winner() == null) return 0;
        if (game.getGipfBoardState().players.winner() == game.getGipfBoardState().players.white) return -1;
        return 1;
    }
}
