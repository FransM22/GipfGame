package AI.Players;

import AI.BoardStateProperties;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Optional;

/**
 * Created by frans on 7-1-2016.
 */
public class LongPlayer extends ComputerPlayer<Integer> {
    public LongPlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("longValue"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE)
            return getMoveWithLowestHeuristicValue(gipfBoardState, false);
        else
            return getMoveWithLowestHeuristicValue(gipfBoardState, true);

    }
}
