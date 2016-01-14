package GameLogic.Loggers;

/**
 * Created by frans on 11-1-2016.
 */
public class ExperimentLogger { // TODO make output faster
    private static final ExperimentLogger INSTANCE = new ExperimentLogger();
    private static long gameNr = 0;

    private ExperimentLogger() {
    }

    public static ExperimentLogger get() {
        return INSTANCE;
    }

    public void log(String debug) {
        System.out.println(gameNr++ + "; " + debug);
    }
}
