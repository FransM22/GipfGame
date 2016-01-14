package GUI2;

import AI.BoardStateProperties;
import AI.Players.*;
import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI2.Threads.CalculateMctsThread;
import GUI2.Threads.UpdateChildrenThread;
import GUI2.Threads.WindowUpdateThread;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
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
    public TreeTableColumn<GipfBoardState, Long> columnBlobPlayerValue;
    public TreeTableColumn<GipfBoardState, Long> columnLongValue;
    public TreeTableColumn<GipfBoardState, Double> columnWeightedValue;
    public ProgressBar thinkingTimeProgress;
    public ProgressBar run100TimesProgressBar;
    public Label boardDescriptionLabel;
    public ToggleButton playButton;
    public Button newGameButton;
    public TreeTableView<GipfBoardState> boardStateTreeTableView;
    public GameAnalyzeTab gameAnalyzeTab;
    public Tab gameTab;
    public ComboBox<Field> whiteHeuristicCombobox;
    public ComboBox<Field> blackHeuristicCombobox;
    public Label whiteInfoLabel;
    public Label blackInfoLabel;
    public MenuItem menuItemNewBasicGame;
    public CheckBox run100TimesCheckbox;

    public Game game;
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
                blackPlayerCombobox,
                run100TimesCheckbox
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

        gameAnalyzeTab.selectedProperty().addListener((observable, oldValue, isSelectedNewValue) -> {
            GipfBoardState gipfBoardState = game.getGipfBoardState();
            // If no game is running, assign values to the nodes
            new Thread(() -> {
                if (!game.automaticPlayThread.isAlive()) {
                    UpdateChildrenThread.appendBoardState(gipfBoardState);
                }
            }).start();
            GenerateNodes generateNodes = new GenerateNodes(Optional.of(gipfBoardState), OptionalInt.of(1), boardStateTreeTableView);

            if (isSelectedNewValue) {
                boardStateTreeTableView.setRoot(generateNodes.root);
            } else {
                CalculateMctsThread.setCurrentRootState(null);
            }
        });
        UpdateChildrenThread.setGameAnalyzeTab(gameAnalyzeTab);

        minThinkingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000, 5000, 5000));


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

                if (run100TimesCheckbox.isSelected()) {
                    gipfBoardComponent.game.startNGameCycles(gipfBoardComponent::repaint, 100);
                } else {
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

    private void startWindowUpdateThread() {
        if (!WindowUpdateThread.propertiesAreSet)
            WindowUpdateThread.setProperties(this);
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
                LongPlayer.class,
                WeightPlayer.class,
                MutualExchangePlayer.class
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

        columnWeightedValue.setCellValueFactory((TreeTableColumn.CellDataFeatures<GipfBoardState, Double> p) -> new ReadOnlyDoubleWrapper(
                p.getValue().getValue().boardStateProperties.weightedHeuristic).asObject());

        try {
            // Long values
            columnDepth.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("depth")));
            columnMinMax.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("minMaxValue")));
            columnBlobPlayerValue.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("blobValue")));
            columnLongValue.setCellValueFactory(cellFactoryLongField(BoardStateProperties.class.getField("longValue")));
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