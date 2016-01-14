package GUI2.Threads;

import AI.Players.ComputerPlayer;
import AI.Players.MCTSPlayer;
import Exceptions.GameEndException;
import GUI2.SettingsSingleton;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.PieceColor;

import java.time.Duration;
import java.time.Instant;
import java.util.OptionalDouble;

/**
 * Created by frans on 14-1-2016.
 */
public class GameLoopThread extends Thread {
    private Game game;
    private Runnable runAfterMove;
    private Instant sleepingUntil = Instant.EPOCH;
    private Instant sleepingSince = Instant.EPOCH;
    private int nrOfGames;

    public GameLoopThread(Game game, Runnable runAfterMove) {
        super(() -> {
            while (true) {
                if (game != null) {
                    // This loop performs all moves in a game
                    // Calculate mcts if the next player requires it
                    ComputerPlayer currentPlayer = game.getGipfBoardState().players.current().pieceColor == PieceColor.WHITE ? game.whitePlayer : game.blackPlayer;

                    CalculateMctsThread.setCurrentRootState(game.getGipfBoardState());

                    if (currentPlayer.getClass() == MCTSPlayer.class) {
                        // if the current player uses the mcts algorithm, let the CalculateMctsThread play games during the sleeping time
                        CalculateMctsThread.setCurrentRootState(game.getGipfBoardState());
                    } else {
                        CalculateMctsThread.setCurrentRootState(null);
                    }

                    try {
                        // The waiting time is artificial, it makes the difference between two moves clearer
                        ((GameLoopThread) Thread.currentThread()).setSleepingInstance(Instant.now(), Instant.now().plusMillis(game.minWaitTime));
                        Thread.sleep(game.minWaitTime);
                    } catch (InterruptedException e) {
                        return; // If the player presses the play button again, the game is stopped immediately.
                    }

                    if (SettingsSingleton.getInstance().showMCTSOutput)
                        System.out.println("Analyzed " + CalculateMctsThread.getGamesPlayedForCurrentRootState() + " games before performing a move");

                    try {
                        game.applyCurrentPlayerMove();
                    } catch (GameEndException e) {
                        return; // If the game is ended, the thread stops immediately.
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

    public OptionalDouble getSleepingProgress() {
        Instant instantNow = Instant.now();

        if (instantNow.isBefore(this.sleepingUntil)) {
            return OptionalDouble.of((double) Duration.between(this.sleepingSince, instantNow).toMillis() / Duration.between(this.sleepingSince, sleepingUntil).toMillis());
        }

        else return OptionalDouble.empty();
    }

    public void setSleepingInstance(Instant sleepingSince, Instant sleepingUntil) {
        this.sleepingSince = sleepingSince;
        this.sleepingUntil = sleepingUntil;
    }
}
