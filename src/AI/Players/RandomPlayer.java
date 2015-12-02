package AI.Players;

import AI.BoardStateProperties;

import java.util.Optional;

/**
 * Created by frans on 13-11-2015.
 */
public class RandomPlayer extends ComputerPlayer<Double> {
    public RandomPlayer() {
        try {
            this.heuristic = Optional.of(BoardStateProperties.class.getField("heuristicRandomValue"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
