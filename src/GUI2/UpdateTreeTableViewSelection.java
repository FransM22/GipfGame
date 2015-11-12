package GUI2;

import GameLogic.GipfBoardState;
import GameLogic.PlayersInGame;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 10-11-2015.
 */
public class UpdateTreeTableViewSelection {
    private final TreeTableView<GipfBoardState> gipfBoardStateTreeTableView;
    private Label boardDescriptionLabel;


    public UpdateTreeTableViewSelection(Label boardDescriptionLabel, TreeTableView<GipfBoardState> gipfBoardStateTreeTableView) {
        this.boardDescriptionLabel = boardDescriptionLabel;
        this.gipfBoardStateTreeTableView = gipfBoardStateTreeTableView;
    }

    public void updateDescriptionLabel() {
        TreeItem<GipfBoardState> gipfBoardStateTreeItem = gipfBoardStateTreeTableView.getSelectionModel().getSelectedItem();
        PlayersInGame players = gipfBoardStateTreeItem.getValue().players;

        String whiteLabelText =
                "WHITE player:\n" +
                "Placing GIPF pieces?\t" + players.get(WHITE).isPlacingGipfPieces + "\n" +
                "Pieces left:\t\t\t" + players.get(WHITE).reserve + "\n" +
                "On turn:\t\t\t\t" + (players.current() == players.get(WHITE)) + "\n\n";

        String blackLabelText =
                "BLACK player:\n" +
                "Placing GIPF pieces?\t" + players.get(BLACK).isPlacingGipfPieces + "\n" +
                "Pieces left:\t\t\t" + players.get(BLACK).reserve + "\n" +
                "On turn:\t\t\t\t" + (players.current() == players.get(BLACK)) + "\n\n";

        String boardLabelText =
                "BOARD:\n" +
                "Depth:\t\t\t\t" + gipfBoardStateTreeTableView.getTreeItemLevel(gipfBoardStateTreeItem) + "\n" +
                "Direct children:\t\t" + gipfBoardStateTreeItem.getChildren().size() + "\n" +
                "Expanded nodes:\t\t" + gipfBoardStateTreeTableView.getExpandedItemCount();

        boardDescriptionLabel.setText(whiteLabelText + blackLabelText + boardLabelText);
    }
}
