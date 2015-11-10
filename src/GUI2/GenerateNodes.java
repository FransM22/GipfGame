package GUI2;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.PieceType;
import javafx.scene.control.TreeItem;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Created by frans on 9-11-2015.
 */
public class GenerateNodes {
    public TreeItem<GipfBoardState> root = new TreeItem<>();

    public GenerateNodes(Optional<GipfBoardState> start, OptionalInt depth) {
        if (start.isPresent()) root.setValue(start.get());
        else root.setValue(new GipfBoardState());

        setChildNodes(root, depth);
    }

    private void setChildNodes(TreeItem<GipfBoardState> treeItem, OptionalInt depth) {
        if (depth.isPresent() && depth.getAsInt() < 1 ) return;

        Game game = new BasicGame();
        game.loadState(treeItem.getValue());

        game.getAllowedMoves().stream().forEach(
                move -> {
                    Game childGame = new BasicGame();
                    childGame.loadState(treeItem.getValue());

                    if (move.addedPiece.getPieceType() == PieceType.GIPF) {
                        childGame.players.current().hasPlacedGipfPieces = true;
                    }

                    childGame.applyMove(move);

                    childGame.storeState(childGame.getGipfBoardState(), childGame.players.current().hasPlacedGipfPieces);

                    TreeItem<GipfBoardState> childItem = new TreeItem<GipfBoardState>(childGame.getGipfBoardState());
                    treeItem.getChildren().add(childItem);

                    this.setChildNodes(childItem, OptionalInt.of(depth.getAsInt() - 1));
                }
        );
    }
}
