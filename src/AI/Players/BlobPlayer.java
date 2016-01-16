package AI.Players;

import AI.BoardStateProperties;

import java.util.Optional;

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
}
