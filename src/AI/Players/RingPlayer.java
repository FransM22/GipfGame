package AI.Players;

import AI.BoardStateProperties;

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
}
