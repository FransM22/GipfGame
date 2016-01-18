package AI.Players;

import AI.BoardStateProperties;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Optional;

/**
 * Created by frans on 8-12-2015.
 */
public class RingPlayer extends ComputerPlayer<Integer> {
    public RingPlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("ringValue"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE)
            return getMoveWithLowestHeuristicValue(gipfBoardState, false);
        else return getMoveWithLowestHeuristicValue(gipfBoardState,true);
    }
}
