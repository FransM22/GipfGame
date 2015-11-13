package GUI2;

import AI.Players.HumanPlayer;
import AI.Players.RandomPlayer;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

public class Controller implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private ComboBox<Class> whitePlayerCombobox;
    @FXML
    private ComboBox<Class> blackPlayerCombobox;
    @FXML
    private SwingNode gipfGameNode;
    @FXML
    private SwingNode smallGipfGameVisualisationNode;
    @FXML
    private TreeTableColumn<GipfBoardState, String> columnBoardName;
    @FXML
    private TreeTableColumn<GipfBoardState, Integer> columnWhiteReserve;
    @FXML
    private TreeTableColumn<GipfBoardState, Integer> columnBlackReserve;
    @FXML
    private TreeTableColumn<GipfBoardState, String> columnCurrentPlayer;
    @FXML
    private TreeTableColumn<GipfBoardState, Boolean> columnWhiteGipf;
    @FXML
    private TreeTableColumn<GipfBoardState, Boolean> columnBlackGipf;
    @FXML
    private TreeTableColumn<GipfBoardState, Boolean> columnIsPruned;
    @FXML
    private TreeTableColumn<GipfBoardState, Integer> columnMinMax;
    @FXML
    private TreeTableColumn<GipfBoardState, Double> columnHeuristic0;
    @FXML
    private TreeTableColumn<GipfBoardState, Integer> columnHeuristic1;
    @FXML
    private Label boardDescriptionLabel;
    @FXML
    private TreeTableView<GipfBoardState> boardStateTreeTableView;
    @FXML
    private Tab analyzeGameTab;

    private Game game;
    private GipfBoardComponent gipfBoardComponent;
    private GipfBoardComponent smallVisualisationComponent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        whitePlayerCombobox.setItems(FXCollections.observableList(Arrays.asList(HumanPlayer.class, RandomPlayer.class)));
        blackPlayerCombobox.setItems(FXCollections.observableList(Arrays.asList(HumanPlayer.class, RandomPlayer.class)));

        game = new BasicGame();
        gipfBoardComponent = new GipfBoardComponent(game, false);
        gipfGameNode.setContent(gipfBoardComponent);
        Game smallVisualisationGame = new BasicGame();
        smallVisualisationComponent = new GipfBoardComponent(smallVisualisationGame, true);
        smallGipfGameVisualisationNode.setContent(smallVisualisationComponent);

        initializeColumns();

        UpdateTreeTableViewSelection updateTreeTableViewSelection = new UpdateTreeTableViewSelection(boardDescriptionLabel, boardStateTreeTableView);

        boardStateTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TreeItem<GipfBoardState> selectedItem = newValue;
                smallVisualisationGame.loadState(selectedItem.getValue());
                updateTreeTableViewSelection.updateDescriptionLabel();
                smallVisualisationComponent.repaint();
            }
        });

        analyzeGameTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            GenerateNodes generateNodes = new GenerateNodes(Optional.of(game.getGipfBoardState()), OptionalInt.of(1), boardStateTreeTableView);
            boardStateTreeTableView.setRoot(generateNodes.root);
        });
    }

    public void repaintGipfBoards() {
        gipfBoardComponent.repaint();
        smallVisualisationComponent.repaint();
    }

    private void initializeColumns() {
        columnBoardName.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                        Integer.toHexString(p.getValue().getValue().hashCode())));

        columnWhiteReserve.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().players.get(WHITE).reserve).asObject());
        columnBlackReserve.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().players.get(BLACK).reserve).asObject());

        columnCurrentPlayer.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                        p.getValue().getValue().players.current().pieceColor.toString()));

        columnWhiteGipf.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Boolean> p) -> new ReadOnlyBooleanWrapper(
                        p.getValue().getValue().players.get(WHITE).hasPlacedNormalPieces).asObject());

        columnBlackGipf.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Boolean> p) -> new ReadOnlyBooleanWrapper(
                        p.getValue().getValue().players.get(BLACK).hasPlacedNormalPieces).asObject());

        columnIsPruned.setCellValueFactory((p) -> new ReadOnlyBooleanWrapper(false).asObject());

        columnMinMax.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().boardStateProperties.minMaxValue).asObject());

        columnHeuristic0.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Double> p) -> new ReadOnlyDoubleWrapper(
                p.getValue().getValue().boardStateProperties.heuristicRandomValue).asObject());

        columnHeuristic1.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().boardStateProperties.heuristicWhiteMinusBlack).asObject());
    }
}