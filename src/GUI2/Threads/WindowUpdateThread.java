package GUI2.Threads;

import GUI2.Controller;
import GUI2.GameAnalyzeTab;
import javafx.application.Platform;

import java.lang.reflect.Field;

/**
 * Created by frans on 11-1-2016.
 */
public class WindowUpdateThread extends Thread {
    private static WindowUpdateThread windowUpdateThread;
    private UpdateChildrenThread updateChildrenThread;
    private GameAnalyzeTab gameAnalyzeTab;
    private Controller controller;

    private WindowUpdateThread() {
        super(() -> {
            WindowUpdateThread windowUpdateThread = WindowUpdateThread.getInstance();
            Controller controller = WindowUpdateThread.getInstance().controller;
            GameAnalyzeTab gameAnalyzeTab = WindowUpdateThread.getInstance().gameAnalyzeTab;

            while (true) {
                try {
                    // If the analyze game tab is selected
                    System.out.println("thread");
                    if (windowUpdateThread.gameAnalyzeTab.selectedProperty().getValue()) {
                        sleep(1);
                        System.out.println("game analyze tab is selected");

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
                    } else if (gameAnalyzeTab.selectedProperty().getValue()) {
                        controller.boardStateTreeTableView.refresh();
                    } else {
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });


        this.setName("WindowUpdateThread");
    }

    public static WindowUpdateThread getInstance() {
        if (windowUpdateThread == null) {
            windowUpdateThread = new WindowUpdateThread();
        }
        return windowUpdateThread;
    }
}
