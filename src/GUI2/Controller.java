package GUI2;

import AI.BoardStateProperties;
import AI.Players.DecisionTreePlayer;
import AI.Players.HumanPlayer;
import AI.Players.MCTSPlayer;
import AI.Players.RandomPlayer;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI2.StringConverters.AlgorithmStringConverter;
import GUI2.StringConverters.HeuristicStringConverter;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

public class Controller implements Initializable {
    @FXML
    private ProgressBar blackProgressBar;
    @FXML
    private ProgressBar whiteProgressBar;
    @FXML
    private Spinner<Integer> maxThinkingTimeSpinner;
    @FXML
    private Spinner<Integer> minThinkingTimeSpinner;
    @FXML
    private ComboBox<Class<? extends Function<GipfBoardState, Move>>> whitePlayerCombobox;
    @FXML
    private ComboBox<Class<? extends Function<GipfBoardState, Move>>> blackPlayerCombobox;
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
    private ToggleButton playButton;
    @FXML
    private TreeTableView<GipfBoardState> boardStateTreeTableView;
    @FXML
    private Tab analyzeGameTab;
    @FXML
    private ComboBox<Field> whiteHeuristicCombobox;
    @FXML
    private ComboBox<Field> blackHeuristicCombobox;

    private Game game;
    private GipfBoardComponent gipfBoardComponent;
    private GipfBoardComponent smallVisualisationComponent;
    private List<Control> controlsToBeInactiveDuringPlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboboxes();

        game = new BasicGame();
        gipfBoardComponent = new GipfBoardComponent(game, false);
        gipfGameNode.setContent(gipfBoardComponent);
        Game smallVisualisationGame = new BasicGame();
        smallVisualisationComponent = new GipfBoardComponent(smallVisualisationGame, true);
        smallGipfGameVisualisationNode.setContent(smallVisualisationComponent);

        controlsToBeInactiveDuringPlay = Arrays.asList(
                whiteHeuristicCombobox,
                blackHeuristicCombobox,
                whitePlayerCombobox,
                blackPlayerCombobox
        );

        initializeColumns();

        UpdateTreeTableViewSelection updateTreeTableViewSelection = new UpdateTreeTableViewSelection(boardDescriptionLabel, boardStateTreeTableView);

        boardStateTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                smallVisualisationGame.loadState(newValue.getValue());
                updateTreeTableViewSelection.updateDescriptionLabel();
                smallVisualisationComponent.repaint();
            }
        });

        analyzeGameTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            GenerateNodes generateNodes = new GenerateNodes(Optional.of(game.getGipfBoardState()), OptionalInt.of(1), boardStateTreeTableView);
            boardStateTreeTableView.setRoot(generateNodes.root);
        });

        minThinkingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 500, 100));
        maxThinkingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 5000, 100));


        /*
        The listener of the play button
         */
        playButton.selectedProperty().addListener((observable, oldValue, isSelected) -> {
            // the minThinkingTimeSPinner must be initialized before this call.
            gipfBoardComponent.game.minWaitTime = minThinkingTimeSpinner.getValue();

            // If the button is not selected
            if (isSelected) {
                setActivatedStateDuringPlay(false);

                try {
                    gipfBoardComponent.game.whitePlayer = whitePlayerCombobox.getValue().newInstance();
                    gipfBoardComponent.game.blackPlayer = blackPlayerCombobox.getValue().newInstance();
                } catch (Exception e) {
                    System.err.println("Could not instantiate player.");
                    e.printStackTrace();
                }
                gipfBoardComponent.game.startGameCycle(gipfBoardComponent::repaint);
            } else {
                setActivatedStateDuringPlay(true);
                gipfBoardComponent.game.automaticPlayThread.interrupt();
            }
        });

        minThinkingTimeSpinner.valueProperty().addListener(((observable, oldValue, newValue) -> gipfBoardComponent.game.minWaitTime = newValue));
    }

    /**
     * Sets up both the player selection comboboxes and the heuristic selection comboboxes
     */
    private void setupComboboxes() {
        // Add the classes that represent the players (those classes must implement the Function<GipfBoardState, Move> interface.
        // Java (without extra libraries) doesn't allow for querying which classes are inside a package, so all players have to be added
        // manually here
        ObservableList<Class<? extends Function<GipfBoardState, Move>>> playerOList = FXCollections.observableList(Arrays.asList(
                RandomPlayer.class,
                HumanPlayer.class,
                MCTSPlayer.class,
                DecisionTreePlayer.class
        ));

        // Because all the heuristics are fields in the BoardStateProperties class, we can add them all automatically.
        ObservableList<Field> heuristicOList = FXCollections.observableList(Arrays.asList(BoardStateProperties.class.getFields()));
        HeuristicStringConverter heuristicStringConverter = new HeuristicStringConverter();
        AlgorithmStringConverter algorithmStringConverter = new AlgorithmStringConverter(playerOList);

        whitePlayerCombobox.setItems(playerOList);
        blackPlayerCombobox.setItems(playerOList);
        whiteHeuristicCombobox.setItems(heuristicOList);
        blackHeuristicCombobox.setItems(heuristicOList);
        whitePlayerCombobox.setConverter(algorithmStringConverter);
        blackPlayerCombobox.setConverter(algorithmStringConverter);
        whiteHeuristicCombobox.setConverter(heuristicStringConverter);
        blackHeuristicCombobox.setConverter(heuristicStringConverter);

        whitePlayerCombobox.setValue(playerOList.get(0));
        whitePlayerCombobox.setValue(playerOList.get(0));
        blackPlayerCombobox.setValue(playerOList.get(0));
        whiteHeuristicCombobox.setValue(heuristicOList.get(0));
        blackHeuristicCombobox.setValue(heuristicOList.get(0));
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

    private void setActivatedStateDuringPlay(boolean setActive) {
        for (Control control : controlsToBeInactiveDuringPlay) {
            control.setDisable(!setActive);
        }
    }
}