package GUI2;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.net.URL;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

public class Controller implements Initializable{
    @FXML
    private SwingNode gipfGameNode;
    @FXML
    private SwingNode smallGipfGameVisualisationNode;
    @FXML private TreeTableColumn<GipfBoardState, String> columnBoardName;
    @FXML private TreeTableColumn<GipfBoardState, Integer> columnWhiteReserve;
    @FXML private TreeTableColumn<GipfBoardState, Integer> columnBlackReserve;
    @FXML private TreeTableColumn<GipfBoardState, String> columnCurrentPlayer;
    @FXML private TreeTableColumn<GipfBoardState, Boolean> columnWhiteGipf;
    @FXML private TreeTableColumn<GipfBoardState, Boolean> columnBlackGipf;
    @FXML private TreeTableColumn<GipfBoardState, Integer> columnHeuristic0;
    @FXML private TreeTableView<GipfBoardState> boardStateTreeTableView;
    @FXML private Label boardDescriptionLabel;
    private Game game;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new BasicGame();
        GipfBoardComponent gipfBoardComponent = new GipfBoardComponent(game);
        gipfGameNode.setContent(gipfBoardComponent);
        Game smallVisualisationGame = new BasicGame();
        GipfBoardComponent smallVisualisationComponent = new GipfBoardComponent(smallVisualisationGame);
        smallGipfGameVisualisationNode.setContent(smallVisualisationComponent);

        columnBoardName.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                        p.getValue().getValue().toString()
                )
        );

        columnWhiteReserve.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().players.get(WHITE).reserve).asObject());
        columnBlackReserve.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().players.get(BLACK).reserve).asObject());


        columnCurrentPlayer.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                        p.getValue().getValue().players.current().pieceColor.toString()
                )
        );

        columnWhiteGipf.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Boolean> p) -> new ReadOnlyBooleanWrapper(
                        p.getValue().getValue().players.get(WHITE).hasPlacedNormalPieces).asObject()
        );

        columnBlackGipf.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Boolean> p) -> new ReadOnlyBooleanWrapper(
                        p.getValue().getValue().players.get(BLACK).hasPlacedNormalPieces).asObject()
        );

        GenerateNodes generateNodes = new GenerateNodes(Optional.of(game.getGipfBoardState()), OptionalInt.of(2));
        boardStateTreeTableView.setRoot(generateNodes.root);

        UpdateTreeTableViewSelection updateTreeTableViewSelection = new UpdateTreeTableViewSelection(boardDescriptionLabel, smallVisualisationComponent);

        boardStateTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<GipfBoardState> selectedItem = (TreeItem<GipfBoardState>) newValue;
            smallVisualisationGame.loadState(selectedItem.getValue());
            updateTreeTableViewSelection.updateDescriptionLabel();
            smallVisualisationComponent.repaint();
        });
    }
}