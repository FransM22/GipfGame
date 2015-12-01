package GameLogic;

import GameLogic.Game.Game;

/**
 * Created by frans on 27-11-2015.
 */
public class EmptyLogger extends GameLogger {
    public EmptyLogger(Game game) {
        super(game);
    }

    @Override
    public void log(String debug) {
        // Don't do anything with it
    }
}
