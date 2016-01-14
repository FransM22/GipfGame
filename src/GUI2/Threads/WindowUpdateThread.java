package GUI2.Threads;

import GUI2.Controller;
import javafx.application.Platform;

import java.lang.reflect.Field;
import java.time.Instant;

/**
 * Created by frans on 11-1-2016.
 */
public class WindowUpdateThread extends Thread {
    public static boolean propertiesAreSet = false;
    private static WindowUpdateThread INSTANCE = new WindowUpdateThread();
    private static Controller controller;
    private static Instant latestUpdatedAt = Instant.EPOCH;

    private WindowUpdateThread() {
        super(() -> {
            Controller controller = WindowUpdateThread.controller;

            while (true) {
                // If the analyze game tab is selected
                if (WindowUpdateThread.controller.gameTab.isSelected()) {
                    // Update player stats
                    // The label update should happen in the FX application thread:
                    Platform.runLater(() -> {
                        String whiteInfoLabelText = "";
                        String blackInfoLabelText = "";

                        if (controller.game.whitePlayer.maxDepth.isPresent()) {
                            whiteInfoLabelText += "Max depth: " + controller.game.whitePlayer.maxDepth.get() + "\n";
                        }
                        if (controller.game.whitePlayer.heuristic.isPresent()) {
                            whiteInfoLabelText += "Heuristic:  " + ((Field) controller.game.whitePlayer.heuristic.get()).getName() + "\n";
                        }
                        whiteInfoLabelText += "Reserve: " + controller.game.getGipfBoardState().players.white.reserve;
                        controller.whiteInfoLabel.setText(whiteInfoLabelText);

                        if (controller.game.blackPlayer.maxDepth.isPresent()) {
                            blackInfoLabelText += "Max depth: " + controller.game.blackPlayer.maxDepth.get() + "\n";
                        }
                        if (controller.game.blackPlayer.heuristic.isPresent()) {
                            blackInfoLabelText += "Heuristic:  " + ((Field) controller.game.blackPlayer.heuristic.get()).getName() + "\n";
                        }
                        blackInfoLabelText += "Reserve: " + controller.game.getGipfBoardState().players.black.reserve;
                        controller.blackInfoLabel.setText(blackInfoLabelText);

                    });
                } else if (controller.gameAnalyzeTab.isSelected()) {
//                    if (UpdateChildrenThread.isActive ||
//                            UpdateChildrenThread.latestUpdatedAt.isAfter(WindowUpdateThread.latestUpdatedAt)) {
                        controller.boardStateTreeTableView.refresh();
//                    }
                }

                latestUpdatedAt = Instant.now();

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    // Keep running
                    e.printStackTrace();
                }
            }
        });


        this.setName("WindowUpdateThread");
        start();
    }

    public static void setProperties(Controller controller) {
        WindowUpdateThread.controller = controller;

        propertiesAreSet = true;
    }

    public static WindowUpdateThread getInstance() {
        return INSTANCE;
    }
}
