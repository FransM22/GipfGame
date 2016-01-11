package GameLogic.Loggers;

import GameLogic.Game.Game;

/**
 * Created by frans on 11-1-2016.
 */
public class ExperimentLogger extends GameLogger {
    private static long gameNr = 0;

    public ExperimentLogger() {
        super();
    }

    @Override
    public void log(String debug) {
        System.out.println(gameNr + "; " + debug);
        // TODO implement the experiment logger
    }
}
