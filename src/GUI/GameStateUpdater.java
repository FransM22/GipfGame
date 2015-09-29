package GUI;

import GUI.GipfBoardComponent.UIval;
import GameLogic.Game;

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
                TimeUnit.MILLISECONDS.sleep(UIval.get().gameStateUpdateIntervalMs);

                while (!game.debugMessages.isEmpty()) {
                    gipfWindow.appendDebugMessage(game.debugMessages.pop());
                }

                gipfWindow.setCurrentPlayerLabel("Current player: " + game.getCurrentPlayer().pieceColor);
                gipfWindow.setPiecesLeftLabel("White pieces left: " + game.whitePlayer.piecesLeft + " | Black pieces left: " + game.blackPlayer.piecesLeft);
            } catch (InterruptedException e) {
                break;  // Break out of the loop
            }
        }
    }
}
