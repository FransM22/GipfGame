package GUI;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Game;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Created by frans on 29-9-2015.
 */
public class GameStateUpdater implements Runnable {
    Game game;
    GipfWindow gipfWindow;

    public GameStateUpdater(GipfWindow gipfWindow, Game game) {
        this.gipfWindow = gipfWindow;
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);

                while (!game.debugMessages.isEmpty()) {
                    gipfWindow.appendDebugMessage(game.debugMessages.pop());
                }

                gipfWindow.setPiecesLeftMessage("Current player: " + game.getCurrentPlayer() + " | " + "Pieces left: " + game.getCurrentPlayer().piecesLeft);
            } catch (InterruptedException e) {
                break;  // Break out of the loop
            }
        }
    }
}
