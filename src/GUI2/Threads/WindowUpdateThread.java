package GUI2.Threads;

import GUI2.Controller;
import javafx.application.Platform;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.OptionalDouble;

/**
 * Created by frans on 11-1-2016.
 */
public class WindowUpdateThread extends Thread {
    public static boolean propertiesAreSet = false;
    private static WindowUpdateThread INSTANCE = new WindowUpdateThread();
    private static Controller controller;

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
                            whiteInfoLabelText += "Max depth: " + controller.game.whitePlayer.maxDepth.getAsInt() + "\n";
                        }
                        if (controller.game.whitePlayer.heuristic.isPresent()) {
                            whiteInfoLabelText += "Heuristic:  " + ((Field) controller.game.whitePlayer.heuristic.get()).getName() + "\n";
                        }
                        whiteInfoLabelText += "Reserve: " + controller.game.getGipfBoardState().players.white.reserve;
                        controller.whiteInfoLabel.setText(whiteInfoLabelText);

                        if (controller.game.blackPlayer.maxDepth.isPresent()) {
                            blackInfoLabelText += "Max depth: " + controller.game.blackPlayer.maxDepth.getAsInt() + "\n";
                        }
                        if (controller.game.blackPlayer.heuristic.isPresent()) {
                            blackInfoLabelText += "Heuristic:  " + ((Field) controller.game.blackPlayer.heuristic.get()).getName() + "\n";
                        }
                        blackInfoLabelText += "Reserve: " + controller.game.getGipfBoardState().players.black.reserve;
                        controller.blackInfoLabel.setText(blackInfoLabelText);


                        // Show how far in the min waiting time progress the player is
                        if (WindowUpdateThread.controller.game.automaticPlayThread != null) {
                            OptionalDouble sleepingProgress = WindowUpdateThread.controller.game.automaticPlayThread.getSleepingProgress();

                            // Refresh rate is too slow to update it when the waiting time is less than 500 ms
                            if (sleepingProgress.isPresent() && WindowUpdateThread.controller.game.minWaitTime > 500) {
                                controller.thinkingTimeProgress.setVisible(true);
                                controller.thinkingTimeProgress.setProgress(sleepingProgress.getAsDouble());
                            } else {
                                controller.thinkingTimeProgress.setVisible(false);
                            }
                        }

                        // Show how far in the N games the player is
                        OptionalDouble NGamesProgress = controller.game.progressOfNGames;
                        if (NGamesProgress.isPresent()) {
                            controller.run100TimesProgressBar.setVisible(true);
                            controller.run100TimesProgressBar.setProgress(NGamesProgress.getAsDouble());
                        }
                        else {
                            controller.run100TimesProgressBar.setVisible(false);
                        }
                    });
                } else if (controller.gameAnalyzeTab.isSelected()) {
                        controller.boardStateTreeTableView.refresh();
                }

                try {
                    sleep(200);
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
}
