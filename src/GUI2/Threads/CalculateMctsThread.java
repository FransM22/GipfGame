package GUI2.Threads;

import AI.AssignPureMCTSValue;
import GameLogic.GipfBoardState;

/**
 * Created by frans on 13-1-2016.
 */
public class CalculateMctsThread extends Thread {
    private static final CalculateMctsThread INSTANCE = new CalculateMctsThread();  // Won't work if line is deleted
    private static GipfBoardState currentRootState;

    private CalculateMctsThread() {
        super(() -> {
            while (true) {
                try {
                    if (currentRootState != null) {
                        currentRootState.exploreAllChildren();
                        new AssignPureMCTSValue().apply(currentRootState);
                    }
                }
                catch (NullPointerException e) {
                    currentRootState = null;
                }
            }
        });

        setName("CalculateMctsThread");
        start();
    }

    public static int getGamesPlayedForCurrentRootState() {
        if (currentRootState == null) return 0;

        return currentRootState.boardStateProperties.mcts_n;
    }

    public static void setCurrentRootState(GipfBoardState newRootState) {
        currentRootState = newRootState;
    }
}
