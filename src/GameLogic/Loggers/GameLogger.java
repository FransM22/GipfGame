package GameLogic.Loggers;

import GameLogic.Game.Game;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Created by frans on 5-10-2015.
 */
public class GameLogger implements Serializable {
    public final LinkedList<String> logMessages;                // Messages displayed in the log in the window (if there is a GipfWindow instance connected to this game)
    private Instant gameStartedTime;
    private static GameLogger gameLogger = new GameLogger();

    GameLogger() {
        logMessages = new LinkedList<>();
    }

    public GameLogger getInstance() {
        return gameLogger;
    }

    public void setGame(Game game) {
        gameStartedTime = Instant.now();
        log("Started a new " + game.getClass() + " GIPF game.");
    }

    public void log(String debug) {
        Duration durationOfGame = Duration.between(gameStartedTime, Instant.now());
        LocalTime time = LocalTime.ofNanoOfDay(durationOfGame.toNanos());
        String timeString = time.format(DateTimeFormatter.ofPattern("[HH:mm:ss.SSS]"));
        logMessages.add(timeString + ": " + debug);
    }

    public boolean isEmpty() {
        return logMessages.isEmpty();
    }
}
