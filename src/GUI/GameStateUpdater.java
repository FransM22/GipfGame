package GUI;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.GipfBoard;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by frans on 29-9-2015.
 */
public class GameStateUpdater implements Runnable {
    GipfBoardComponent gipfBoardComponent;
    GipfWindow gipfWindow;

    public GameStateUpdater(GipfBoardComponent gipfBoardComponent, GipfWindow gipfWindow) {
        this.gipfBoardComponent = gipfBoardComponent;
        this.gipfWindow = gipfWindow;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);

                gipfWindow.addDebugInfo("New debug info at " + LocalDateTime.now());
            } catch (InterruptedException e) {
                break;  // Break out of the loop
            }
        }
    }
}
