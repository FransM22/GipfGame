package AI.Players;

import AI.BoardStateProperties;
import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.util.Optional;

import static GameLogic.PieceColor.*;

/**
 * Created by frans on 8-12-2015.
 */
public class BlobPlayer extends ComputerPlayer<Long> {
    public BlobPlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("blobValue"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO this may maybe in the future improve the AI but for now it increases the time a game takes to be played
     * @param isNondetermenistic
     */
    public BlobPlayer(boolean isNondetermenistic) {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("blobValue"));
            this.isNondetermenistic = isNondetermenistic;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        // White minimizes, black maximizes
        if (gipfBoardState.players.current().pieceColor == WHITE)
            return super.getMoveWithLowestHeuristicValue(gipfBoardState, false);
        else
            return super.getMoveWithLowestHeuristicValue(gipfBoardState, true);
    }
}
