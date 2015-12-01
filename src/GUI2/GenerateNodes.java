package GUI2;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Created by frans on 9-11-2015.
 */
class GenerateNodes {
    public final TreeItem<GipfBoardState> root = new TreeItem<>();
    private final TreeTableView<GipfBoardState> treeTableView;

    public GenerateNodes(Optional<GipfBoardState> start, OptionalInt depth, TreeTableView<GipfBoardState> boardStateTreeTableView) {
        this.treeTableView = boardStateTreeTableView;


        if (start.isPresent()) {
            root.setValue(start.get());
        } else {
            root.setValue(new GipfBoardState());
        }


        // Update the board state in another thread (so the GUI will stay responding)
        new Thread(() -> {
            setChildNodes(root, depth);
        }).start();
    }

    private void setChildNodes(TreeItem<GipfBoardState> treeItem, OptionalInt depth) {
        if (depth.isPresent() && depth.getAsInt() < 1) return;

        Game game = new BasicGame();
        game.loadState(treeItem.getValue());

        game.getGipfBoardState().exploreAllChildren();

        for (GipfBoardState gipfBoardState : game.getGipfBoardState().exploredChildren.values()) {
            if (!treeItem.getChildren().contains(gipfBoardState)) {
                TreeItem<GipfBoardState> childItem = new TreeItem<>(gipfBoardState);
                treeItem.getChildren().add(childItem);

                treeItem.expandedProperty().addListener(((observable, oldValue, newValue) -> {
                    if (childItem.getChildren().size() == 0) {
                        childItem.getValue().boardStateProperties.updateBoardState();
                        this.setChildNodes(childItem, OptionalInt.of(1));
                    }
                }));
            }
        }

        if (treeTableView.getComparator() != null) {
            treeItem.getChildren().sort(treeTableView.getComparator());
        }
    }
}
