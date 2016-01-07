package AI.Players;

import AI.BoardStateProperties;

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
}
