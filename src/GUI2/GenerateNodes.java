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
public class GenerateNodes {
    public TreeItem<GipfBoardState> root = new TreeItem<>();
    public TreeTableView<GipfBoardState> treeTableView;

    public GenerateNodes(Optional<GipfBoardState> start, OptionalInt depth, TreeTableView<GipfBoardState> boardStateTreeTableView) {
        this.treeTableView = boardStateTreeTableView;

        if (start.isPresent()) root.setValue(start.get());
        else root.setValue(new GipfBoardState());

        setChildNodes(root, depth);
    }

    private void setChildNodes(TreeItem<GipfBoardState> treeItem, OptionalInt depth) {
        if (depth.isPresent() && depth.getAsInt() < 1) return;

        Game game = new BasicGame(false);
        game.loadState(treeItem.getValue());

        game.getAllowedMoves().stream().forEach(
                move -> {
                    Game childGame = new BasicGame(false);
                    childGame.loadState(treeItem.getValue());

                    childGame.applyMove(move);

                    TreeItem<GipfBoardState> childItem = new TreeItem<GipfBoardState>(childGame.getGipfBoardState());


                    treeItem.getChildren().add(childItem);
                    treeItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                        if (childItem.getChildren().size() == 0) {  // Calculate the children if it doesn't have any yet
                            this.setChildNodes(childItem, OptionalInt.of(1));
                        }
                    });
                }
        );

        if (treeTableView.getComparator() != null) {
            treeItem.getChildren().sort(treeTableView.getComparator());
        }
    }
}
