package AI.Players;

import AI.BoardStateProperties;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Optional;

/**
 * Created by frans on 2-12-2015.
 */
public class BlackMinusWhitePlayer extends ComputerPlayer<Integer> {
    public BlackMinusWhitePlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("heuristicBlackMinusWhite"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Move apply(GipfBoardState gipfBoardState) {
        if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE) {
            return getMoveWithLowestHeuristicValue(gipfBoardState, false);
        }
        else {
            return getMoveWithLowestHeuristicValue(gipfBoardState, true);
        }
    }
}
