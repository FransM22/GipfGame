package GUI;

import GameLogic.Game.Game;

import java.util.concurrent.TimeUnit;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

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

                while (!game.getGameLogger().isEmpty()) {
                    gipfWindow.gameLogTextArea.append(game.getGameLogger().logMessages.pop());
                }

                gipfWindow.setCurrentPlayerLabel("Current player: " + game.players.current().pieceColor);
                gipfWindow.setPiecesLeftLabel("White reserve: " + game.players.get(WHITE).getReserve() + " | Black reserve: " + game.players.get(BLACK).getReserve());
                gipfWindow.setGameTypeLabel("Game type: " + game.getGameType());
            } catch (InterruptedException e) {
                break;  // Break out of the loop
            }
        }
    }
}
