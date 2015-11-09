package GUI2;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.net.URL;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    private SwingNode gipfGameNode;
    @FXML private TreeTableColumn<GipfBoardState, String> columnBoardName;
    @FXML private TreeTableColumn<GipfBoardState, Integer> columnHeuristic0;
    @FXML private TreeTableView<GipfBoardState> boardStateTreeTableView;
    private Game game;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new BasicGame();
        GipfBoardComponent gipfBoardComponent = new GipfBoardComponent(game);
        gipfGameNode.setContent(gipfBoardComponent);

        columnBoardName.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                        p.getValue().getValue().toString()
                )
        );

        columnHeuristic0.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().players.current().reserve).asObject());

        GenerateNodes generateNodes = new GenerateNodes(Optional.of(game.getGipfBoardState()), OptionalInt.of(1));
        boardStateTreeTableView.setRoot(generateNodes.root);
    }
}
