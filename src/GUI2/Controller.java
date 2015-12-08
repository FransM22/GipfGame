package GUI2;

import AI.AssignPureMCTSValue;
import AI.BoardStateProperties;
import AI.Players.*;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI2.StringConverters.AlgorithmStringConverter;
import GUI2.StringConverters.HeuristicStringConverter;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import javafx.application.Platform;
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

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

public class Controller implements Initializable {
    @FXML
    private Spinner<Integer> maxThinkingTimeSpinner;
    @FXML
    private Spinner<Integer> minThinkingTimeSpinner;
    @FXML
    private ComboBox<Class<? extends ComputerPlayer>> whitePlayerCombobox;
    @FXML
    private ComboBox<Class<? extends ComputerPlayer>> blackPlayerCombobox;
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
    private TreeTableColumn<GipfBoardState, String> columnMctsWN;
    @FXML
    private TreeTableColumn<GipfBoardState, Integer> columnDepth;
    @FXML
    private TreeTableColumn<GipfBoardState, Double> columnMctsValue;
    @FXML
    private Label boardDescriptionLabel;
    @FXML
    private ToggleButton playButton;
    @FXML
    private TreeTableView<GipfBoardState> boardStateTreeTableView;
    @FXML
    private Tab analyzeGameTab;
    @FXML
    private Tab gameTab;
    @FXML
    private ComboBox<Field> whiteHeuristicCombobox;
    @FXML
    private ComboBox<Field> blackHeuristicCombobox;
    @FXML
    private Label whiteInfoLabel;
    @FXML
    private Label blackInfoLabel;
    @FXML
    private MenuItem menuItemNewBasicGame;

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
            GipfBoardState gipfBoardState = game.getGipfBoardState();
            // If no game is running, assign values to the nodes
            new Thread(() -> {
                if (!game.automaticPlayThread.isAlive()) {
                    gipfBoardState.boardStateProperties.updateChildren();
                    new AssignPureMCTSValue().apply(gipfBoardState);
                }
            }).start();
            GenerateNodes generateNodes = new GenerateNodes(Optional.of(gipfBoardState), OptionalInt.of(1), boardStateTreeTableView);
            boardStateTreeTableView.setRoot(generateNodes.root);
        });

        minThinkingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 100, 100));
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

        setupMenu();
        startWindowUpdateThread();
    }

    private void setupMenu() {
        menuItemNewBasicGame.setOnAction((p) -> {
                    game = new BasicGame();
                    gipfBoardComponent.game = game;
                    repaintGipfBoards();
                }
        );
    }

    private void startWindowUpdateThread() {
        new Thread(() -> {
            boolean fastUpdateRate = true;
            while (true) {
                try {
                    // If the analyze game tab is selected
                    if (gameTab.selectedProperty().getValue()) {
                        fastUpdateRate = true;
                        // Update player stats

                        // The label update should happen in the FX application thread:
                        Platform.runLater(() -> {

                            String whiteInfoLabelText = "";
                            String blackInfoLabelText = "";

                            if (game.whitePlayer.maxDepth.isPresent()) {
                                whiteInfoLabelText += "Max depth: " + game.whitePlayer.maxDepth.get() + "\n";
                            }
                            if (game.whitePlayer.heuristic.isPresent()) {
                                whiteInfoLabelText += "Heuristic:  " + ((Field) game.whitePlayer.heuristic.get()).getName() + "\n";
                            }
                            whiteInfoLabelText += "Reserve: " + game.getGipfBoardState().players.white.reserve;
                            whiteInfoLabel.setText(whiteInfoLabelText);

                            if (game.blackPlayer.maxDepth.isPresent()) {
                                blackInfoLabelText += "Max depth: " + game.blackPlayer.maxDepth.get() + "\n";
                            }
                            if (game.blackPlayer.heuristic.isPresent()) {
                                blackInfoLabelText += "Heuristic:  " + ((Field) game.blackPlayer.heuristic.get()).getName() + "\n";
                            }
                            blackInfoLabelText += "Reserve: " + game.getGipfBoardState().players.black.reserve;
                            blackInfoLabel.setText(blackInfoLabelText);

                        });
                    } else if (analyzeGameTab.selectedProperty().getValue()) {
                        if (game.automaticPlayThread.isAlive() || game.getGipfBoardState().boardStateProperties.isExploringChildren) {
                            fastUpdateRate = true;
                        }
                        else {
                            fastUpdateRate = false;
                        }
                        boardStateTreeTableView.refresh();
                    }
                    else {
                        fastUpdateRate = false;
                    }
                    Thread.sleep((fastUpdateRate ? 200 : 1000));
                } catch (InterruptedException e) {
                    break;
                }
            }

        }).start();
    }

    /**
     * Sets up both the player selection comboboxes and the heuristic selection comboboxes
     */
    private void setupComboboxes() {
        // Add the classes that represent the players (those classes must implement the Function<GipfBoardState, Move> interface.
        // Java (without extra libraries) doesn't allow for querying which classes are inside a package, so all players have to be added
        // manually here
        ObservableList<Class<? extends ComputerPlayer>> playerOList = FXCollections.observableList(Arrays.asList(
                RandomPlayer.class,
                HumanPlayer.class,
                MCTSPlayer.class,
                DecisionTreePlayer.class,
                MinimaxPlayer.class,
                WhiteMinusBlackPlayer.class
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

        // MCTS VALUES
        columnMctsValue.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Double> p) -> new ReadOnlyDoubleWrapper(
                p.getValue().getValue().boardStateProperties.mctsValue).asObject());
        columnMctsWN.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                p.getValue().getValue().boardStateProperties.mcts_w + "/" + p.getValue().getValue().boardStateProperties.mcts_n));
        columnDepth.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Integer> p) -> new ReadOnlyIntegerWrapper(
                p.getValue().getValue().boardStateProperties.depth).asObject());
    }

    private void setActivatedStateDuringPlay(boolean setActive) {
        for (Control control : controlsToBeInactiveDuringPlay) {
            control.setDisable(!setActive);
        }
    }
}