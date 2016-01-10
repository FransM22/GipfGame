package GUI2;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

/**
 * Created by frans on 7-1-2016.
 */
public class GameAnalyzeTab extends Tab {
    ProgressBar progressBar;

    public GameAnalyzeTab() {
        progressBar = new ProgressBar();

        progressBar.setVisible(false);


        HBox tabTitleHbox = new HBox();
        tabTitleHbox.setAlignment(Pos.CENTER_LEFT);
        tabTitleHbox.setSpacing(10);
        tabTitleHbox.getChildren().addAll(
                new Label("Analyze game"),
                progressBar
        );

        setGraphic(tabTitleHbox);
    }

    public void setIsProgressing(boolean isProgressing) {
        if (isProgressing) {
            progressBar.setVisible(true);
        } else {
            progressBar.setVisible(false);
        }
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }
}
