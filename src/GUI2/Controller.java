package GUI2;

import AI.AssignPureMCTSValue;
import AI.BoardStateProperties;
import AI.Players.*;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI2.StringConverters.AlgorithmStringConverter;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

public class Controller implements Initializable {
    // These come from the mainGui.fxml file
    public Spinner<Integer> maxThinkingTimeSpinner;
    public Spinner<Integer> minThinkingTimeSpinner;
    public ComboBox<Class<? extends ComputerPlayer>> whitePlayerCombobox;
    public ComboBox<Class<? extends ComputerPlayer>> blackPlayerCombobox;
    public SwingNode gipfGameNode;
    public SwingNode smallGipfGameVisualisationNode;
    public TreeTableColumn<GipfBoardState, String> columnBoardName;
    public TreeTableColumn<GipfBoardState, Integer> columnWhiteReserve;
    public TreeTableColumn<GipfBoardState, Integer> columnBlackReserve;
    public TreeTableColumn<GipfBoardState, String> columnCurrentPlayer;
    public TreeTableColumn<GipfBoardState, Boolean> columnWhiteGipf;
    public TreeTableColumn<GipfBoardState, Boolean> columnBlackGipf;
    public TreeTableColumn<GipfBoardState, Boolean> columnIsPruned;
    public TreeTableColumn<GipfBoardState, Long> columnMinMax;
    public TreeTableColumn<GipfBoardState, Double> columnHeuristic0;
    public TreeTableColumn<GipfBoardState, Long> columnWhiteMinusBlack;
    public TreeTableColumn<GipfBoardState, String> columnMctsWN;
    public TreeTableColumn<GipfBoardState, Long> columnDepth;
    public TreeTableColumn<GipfBoardState, Double> columnMctsValue;
    public TreeTableColumn<GipfBoardState, Long> columnRingValue;
    public TreeTableColumn<GipfBoardState, Long> blobPlayerValue;
    public TreeTableColumn<GipfBoardState, Long> longValue;
    public Label boardDescriptionLabel;
    public ToggleButton playButton;
    public Button newGameButton;
    public TreeTableView<GipfBoardState> boardStateTreeTableView;
    public GameAnalyzeTab analyzeGameTab;
    public Tab gameTab;
    public ComboBox<Field> whiteHeuristicCombobox;
    public ComboBox<Field> blackHeuristicCombobox;
    public Label whiteInfoLabel;
    public Label blackInfoLabel;
    public MenuItem menuItemNewBasicGame;
    public CheckBox run50timesCheckBox;

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
                    UpdateChildrenThread.getInstance().appendBoardState(gipfBoardState);
                    new AssignPureMCTSValue().apply(gipfBoardState);
                }
            }).start();
            GenerateNodes generateNodes = new GenerateNodes(Optional.of(gipfBoardState), OptionalInt.of(1), boardStateTreeTableView);
            boardStateTreeTableView.setRoot(generateNodes.root);
        });
        UpdateChildrenThread.getInstance().setGameAnalyzeTab(analyzeGameTab);

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

                if (run50timesCheckBox.isSelected()) {
                    gipfBoardComponent.game.startNGameCycles(gipfBoardComponent::repaint, 50);
                }
                else {
                    gipfBoardComponent.game.startGameCycle(gipfBoardComponent::repaint);
                }
            } else {
                setActivatedStateDuringPlay(true);
                gipfBoardComponent.game.automaticPlayThread.interrupt();
            }
        });
        newGameButton.setOnAction((p) -> {
            if (playButton.isSelected()) {
                playButton.fire();
            }
            game = new BasicGame();
            gipfBoardComponent.game = game;
            repaintGipfBoards();
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

    // TODO refactor
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
                        } else {
                            fastUpdateRate = false;
                        }
                        boardStateTreeTableView.refresh();
                    } else {
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
                WhiteMinusBlackPlayer.class,
                RingPlayer.class,
                BlobPlayer.class,
                LongPlayer.class
        ));

        // Because all the heuristics are fields in the BoardStateProperties class, we can add them all automatically.
        ObservableList<Field> heuristicOList = FXCollections.observableList(Arrays.asList(BoardStateProperties.class.getFields()));
        AlgorithmStringConverter algorithmStringConverter = new AlgorithmStringConverter(playerOList);

        whitePlayerCombobox.setItems(playerOList);
        blackPlayerCombobox.setItems(playerOList);
        whiteHeuristicCombobox.setItems(heuristicOList);
        blackHeuristicCombobox.setItems(heuristicOList);
        whitePlayerCombobox.setConverter(algorithmStringConverter);
        blackPlayerCombobox.setConverter(algorithmStringConverter);

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

        columnHeuristic0.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Double> p) -> new ReadOnlyDoubleWrapper(
                p.getValue().getValue().boardStateProperties.heuristicRandomValue).asObject());

        try {
            // Long values
            columnDepth.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("depth")));
            columnMinMax.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("minMaxValue")));
            blobPlayerValue.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("blobValue")));
            longValue.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("longValue")));
            columnRingValue.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("ringValue")));
            columnWhiteMinusBlack.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("heuristicWhiteMinusBlack")));

            // Double values
            columnMctsValue.setCellValueFactory(cellFactoryDoubleField(BoardStateProperties.class.getField("mctsValue")));
        } catch (NoSuchFieldException e) {
            System.err.println("Can't access field");
            e.printStackTrace();
        }

        columnMctsWN.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> new ReadOnlyStringWrapper(
                p.getValue().getValue().boardStateProperties.mcts_w + "/" + p.getValue().getValue().boardStateProperties.mcts_n));
    }

    private void setActivatedStateDuringPlay(boolean setActive) {
        for (Control control : controlsToBeInactiveDuringPlay) {
            control.setDisable(!setActive);
        }
    }

    private Callback<TreeTableColumn.CellDataFeatures<GipfBoardState, Long>, ObservableValue<Long>> cellFactoryLongField(Field field) {
        return (TreeTableColumn.CellDataFeatures<GipfBoardState, Long> p) -> {
            try {
                return new ReadOnlyLongWrapper((Long) field.get(p.getValue().getValue().boardStateProperties)).asObject();
            } catch (IllegalAccessException e) {
                System.out.println("Can't access the field " + field);
                return null;
            }
        };
    }

    private Callback<TreeTableColumn.CellDataFeatures<GipfBoardState, Double>, ObservableValue<Double>> cellFactoryDoubleField(Field field) {
        return (TreeTableColumn.CellDataFeatures<GipfBoardState, Double> p) -> {
            try {
                return new ReadOnlyDoubleWrapper((Double) field.get(p.getValue().getValue().boardStateProperties)).asObject();
            } catch (IllegalAccessException e) {
                System.out.println("Can't access the field " + field);
                return null;
            }
        };
    }

    private Callback<TreeTableColumn.CellDataFeatures<GipfBoardState, String>, ObservableValue<String>> cellFactoryStringField(Field field) {
        return (TreeTableColumn.CellDataFeatures<GipfBoardState, String> p) -> {
            try {
                return new ReadOnlyStringWrapper((String) field.get(p.getValue().getValue().boardStateProperties.toString()));
            } catch (IllegalAccessException e) {
                System.out.println("Can't access the field " + field);
                return null;
            }
        };
    }
}