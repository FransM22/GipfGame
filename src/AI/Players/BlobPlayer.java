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
}
