package AI.Players;

import AI.BoardStateProperties;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Optional;

/**
 * Created by frans on 17-1-2016.
 */
public class WeightedMinMax extends ComputerPlayer<Long> {
    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        try {
            if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE) {
                // White plays the minimizing player, so it wants to minimize the maximum value
                this.heuristic = Optional.of(BoardStateProperties.class.getField("heuristicMax"));
                return getMoveWithLowestHeuristicValue(gipfBoardState, false);

            } else if (gipfBoardState.players.current().pieceColor == PieceColor.BLACK) {
                this.heuristic = Optional.of(BoardStateProperties.class.getField("heuristicMin"));
                return getMoveWithLowestHeuristicValue(gipfBoardState, true);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // Should not happen
        assert false;
        return null;
    }
}
