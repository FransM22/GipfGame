package GUI2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGui.fxml"));
        String css = this.getClass().getResource("treeTableView.css").toExternalForm();

        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add(css);

        primaryStage.setTitle("GIPF Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
         * JavaFX doesn't go entirely well with Swing. Need to repaint the swing components after the scene has been drawn
         */
        new Thread(() -> {
            try {
                Thread.sleep(50);
                ((Controller) loader.getController()).repaintGipfBoards();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
