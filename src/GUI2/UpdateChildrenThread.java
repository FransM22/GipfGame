package GUI2;

import GameLogic.GipfBoardState;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by frans on 8-1-2016.
 */
public class UpdateChildrenThread extends Thread {
    private static final UpdateChildrenThread updateChildrenThread = new UpdateChildrenThread();
    private Queue<GipfBoardState> boardStatesToUpdate;
    private GameAnalyzeTab gameAnalyzeTab;
    private boolean isActive = false;

    private UpdateChildrenThread() {
        super(() -> {
            UpdateChildrenThread updateChildrenThread = UpdateChildrenThread.getInstance();

            while (true) {
                if (updateChildrenThread.isActive) {
                    while (!updateChildrenThread.boardStatesToUpdate.isEmpty()) {
                        GipfBoardState currentGipfBoardState = updateChildrenThread.boardStatesToUpdate.poll();
                        currentGipfBoardState.boardStateProperties.updateChildren();
                    }

                    if (updateChildrenThread.gameAnalyzeTab != null) {
                        updateChildrenThread.gameAnalyzeTab.setIsProgressing(false);
                    }

                    updateChildrenThread.setIsActive(false);
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

    public static UpdateChildrenThread getInstance() {
        return updateChildrenThread;
    }

    public void setGameAnalyzeTab(GameAnalyzeTab gameAnalyzeTab) {
        this.gameAnalyzeTab = gameAnalyzeTab;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void appendBoardState(GipfBoardState gipfBoardState) {
        boardStatesToUpdate.add(gipfBoardState);

        if (!isActive) {
            if (gameAnalyzeTab != null) gameAnalyzeTab.setIsProgressing(true);
            setIsActive(true);
        }
    }
}
