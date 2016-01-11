package GameLogic.Loggers;

import GameLogic.Game.Game;

/**
 * Created by frans on 11-1-2016.
 */
public class ExperimentLogger {
    private static long gameNr = 0;
    private static final ExperimentLogger INSTANCE = new ExperimentLogger();

    private ExperimentLogger() {}

    public static ExperimentLogger get() {
        return INSTANCE;
    }

    public void log(String debug) {
        System.out.println(gameNr++ + "; " + debug);
    }
}
