package GUI;

import GameLogic.Game.Game;

import java.util.concurrent.TimeUnit;

/**
 * Created by frans on 29-9-2015.
 */
class GameStateUpdater implements Runnable {
    private Game game;
    private final GipfWindow gipfWindow;

    public GameStateUpdater(GipfWindow gipfWindow, Game game) {
        this.gipfWindow = gipfWindow;
        this.game = game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(UIval.get().gameStateUpdateIntervalMs);

                while (!game.logMessages.isEmpty()) {
                    gipfWindow.gameLogTextArea.append(game.logMessages.pop());
                }

                gipfWindow.setCurrentPlayerLabel("Current player: " + game.getCurrentPlayer().pieceColor);
                gipfWindow.setPiecesLeftLabel("White pieces left: " + game.whitePlayer.piecesLeft + " | Black pieces left: " + game.blackPlayer.piecesLeft);
                gipfWindow.setGameTypeLabel("Game type: " + game.getGameType());
            } catch (InterruptedException e) {
                break;  // Break out of the loop
            }
        }
    }
}
