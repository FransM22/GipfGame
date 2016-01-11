package AI.Players;

import AI.BoardStateProperties;
import GUI2.Threads.UpdateChildrenThread;
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
        UpdateChildrenThread.getInstance().appendBoardState(gipfBoardState);

        if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE) {
            return getMoveWithHighestHeuristicValue(gipfBoardState, false);
        }
        else {
            return getMoveWithHighestHeuristicValue(gipfBoardState, true);
        }
    }
}
