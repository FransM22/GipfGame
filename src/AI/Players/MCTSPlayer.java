package AI.Players;

import AI.BoardStateProperties;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Optional;

/**
 * Created by Dingding.
 */
public class MCTSPlayer extends ComputerPlayer<Double> {
    public MCTSPlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("mctsValue"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE) {
            return getMoveWithLowestHeuristicValue(gipfBoardState, true);
        }
        else {
            return getMoveWithLowestHeuristicValue(gipfBoardState, false);
        }
    }
}
