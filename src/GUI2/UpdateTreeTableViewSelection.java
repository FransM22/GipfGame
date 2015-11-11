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
    private Label whiteLabel;
    private Label blackLabel;
    private Label boardDescriptionLabel;


    public UpdateTreeTableViewSelection(Label whiteLabel, Label blackLabel, Label boardDescriptionLabel, TreeTableView<GipfBoardState> gipfBoardStateTreeTableView) {
        this.whiteLabel = whiteLabel;
        this.blackLabel = blackLabel;
        this.gipfBoardStateTreeTableView = gipfBoardStateTreeTableView;
        this.boardDescriptionLabel = boardDescriptionLabel;
    }

    public void updateDescriptionLabel() {
        TreeItem<GipfBoardState> gipfBoardStateTreeItem = gipfBoardStateTreeTableView.getSelectionModel().getSelectedItem();
        PlayersInGame players = gipfBoardStateTreeItem.getValue().players;

        String whiteLabelText =
                "Placing GIPF pieces? " + players.get(WHITE).isPlacingGipfPieces + "\n" +
                "Pieces left:         " + players.get(WHITE).reserve + "\n" +
                "On turn:             " + (players.current() == players.get(WHITE));

        String blackLabelText =
                "Placing GIPF pieces? " + players.get(BLACK).isPlacingGipfPieces + "\n" +
                "Pieces left:         " + players.get(BLACK).reserve + "\n" +
                "On turn:             " + (players.current() == players.get(BLACK));

        String boardLabelText =
                "Depth:               " + gipfBoardStateTreeTableView.getTreeItemLevel(gipfBoardStateTreeItem) + "\n" +
                "Direct children:     " + gipfBoardStateTreeItem.getChildren().size() + "\n" +
                "Expanded nodes:      " + gipfBoardStateTreeTableView.getExpandedItemCount();

        whiteLabel.setText(whiteLabelText);
        blackLabel.setText(blackLabelText);
        boardDescriptionLabel.setText(boardLabelText);
    }
}
