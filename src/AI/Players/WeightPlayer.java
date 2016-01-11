package AI.Players;

import AI.BoardStateProperties;

import java.util.Optional;

/**
 * Created by frans on 11-1-2016.
 */
public class WeightPlayer extends ComputerPlayer<Double> {
    public WeightPlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("weightedHeuristic"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
