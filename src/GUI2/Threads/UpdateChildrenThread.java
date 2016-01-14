package GUI2.Threads;

import GUI2.GameAnalyzeTab;
import GameLogic.GipfBoardState;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by frans on 8-1-2016.
 */
public class UpdateChildrenThread extends Thread {
    private static final UpdateChildrenThread INSTANCE = new UpdateChildrenThread();
    private static Queue<GipfBoardState> boardStatesToUpdate;
    private static GameAnalyzeTab gameAnalyzeTab;
    public static boolean isActive = false;
    public static Instant latestUpdatedAt = Instant.EPOCH;

    private UpdateChildrenThread() {
        super(() -> {
            while (true) {
                if (isActive) {
                    while (!boardStatesToUpdate.isEmpty()) {
                        GipfBoardState currentGipfBoardState = boardStatesToUpdate.poll();
                        currentGipfBoardState.boardStateProperties.updateChildren();
                        latestUpdatedAt = Instant.now();
                    }

                    setIsActive(false);
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // Don't act on the interruptedexception. The tread remains active so the same thread can be used
                        // while the program is active
                    }
                }
            }
        });

        setName("Thread-UpdateChildrenThread");
        boardStatesToUpdate = new ConcurrentLinkedQueue<>();

        start();
    }

    public static void setGameAnalyzeTab(GameAnalyzeTab gameAnalyzeTab) {
        UpdateChildrenThread.gameAnalyzeTab = gameAnalyzeTab;
    }

    public static void setIsActive(boolean isActive) {
        UpdateChildrenThread.isActive = isActive;
    }

    public static void appendBoardState(GipfBoardState gipfBoardState) {
        boardStatesToUpdate.add(gipfBoardState);

        if (!isActive) {
            if (gameAnalyzeTab != null) gameAnalyzeTab.setIsProgressing(true);
            setIsActive(true);
        }
    }
}
