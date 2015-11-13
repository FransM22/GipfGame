package GUI2;

import GameLogic.GipfBoardState;
import GameLogic.PlayersInGame;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

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
                        "Placing GIPF pieces?\t" + players.white.isPlacingGipfPieces + "\n" +
                        "Pieces left:\t\t\t" + players.white.reserve + "\n" +
                        "On turn:\t\t\t\t" + (players.current() == players.white) + "\n" +
                        "Is winner: \t\t\t" + (players.winner() == players.white) + "\n\n";

        String blackLabelText =
                "BLACK player:\n" +
                        "Placing GIPF pieces?\t" + players.black.isPlacingGipfPieces + "\n" +
                        "Pieces left:\t\t\t" + players.black.reserve + "\n" +
                        "On turn:\t\t\t\t" + (players.current() == players.black) + "\n" +
                        "Is winner: \t\t\t" + (players.winner() == players.black) + "\n\n";

        String boardLabelText =
                "BOARD:\n" +
                        "Depth:\t\t\t\t" + gipfBoardStateTreeTableView.getTreeItemLevel(gipfBoardStateTreeItem) + "\n" +
                        "Direct children:\t\t" + gipfBoardStateTreeItem.getChildren().size() + "\n" +
                        "Visible nodes:\t\t\t" + gipfBoardStateTreeTableView.getExpandedItemCount();

        boardDescriptionLabel.setText(whiteLabelText + blackLabelText + boardLabelText);
    }
}
