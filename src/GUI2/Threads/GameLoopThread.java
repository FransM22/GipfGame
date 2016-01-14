package GUI2.Threads;

import Exceptions.GameEndException;
import GUI2.SettingsSingleton;
import GameLogic.Game.Game;

/**
 * Created by frans on 14-1-2016.
 */
public class GameLoopThread extends Thread {
    private Game game;
    private Runnable runAfterMove;

    public GameLoopThread(Game game, Runnable runAfterMove) {
        super(() -> {
            while (true) {
                if (game != null) {
                    // This loop performs all moves in a game
                    // Calculate mcts if the next player requires it
                    CalculateMctsThread.setCurrentRootState(game.getGipfBoardState());

                    try {
                        // The waiting time is artificial, it makes the difference between two moves clearer
                        Thread.sleep(game.minWaitTime);
                    } catch (InterruptedException e) {
                        System.out.println("Break");
                    }

                    if (SettingsSingleton.getInstance().showMCTSOutput)
                        System.out.println("Analyzed " + CalculateMctsThread.getGamesPlayedForCurrentRootState() + " games before performing a move");

                    try {
                        game.applyCurrentPlayerMove();
                    } catch (GameEndException e) {
                        return;
                    }

                    // A final action to be executed (for example repainting the component)
                    if (runAfterMove != null) {
                        runAfterMove.run();
                    }
                }
            }
        });

        setName("GameLoopThread");
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setRunAfterMove(Runnable runAfterMove) {
        this.runAfterMove = runAfterMove;
    }
}
