package GUI2;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.PlayersInGame;
import javafx.scene.control.Label;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 10-11-2015.
 */
public class UpdateTreeTableViewSelection {
    private Label whiteLabel;
    private Label blackLabel;
    private GipfBoardComponent gipfBoardComponent;


    public UpdateTreeTableViewSelection(Label whiteLabel, Label blackLabel, GipfBoardComponent gipfBoardComponent) {
        this.whiteLabel = whiteLabel;
        this.blackLabel = blackLabel;
        this.gipfBoardComponent = gipfBoardComponent;
    }

    public void updateDescriptionLabel() {
        PlayersInGame players = gipfBoardComponent.game.players;

        String whiteLabelText =
                "Placing GIPF pieces? " + players.get(WHITE).isPlacingGipfPieces + "\n" +
                "Pieces left:         " + players.get(WHITE).reserve + "\n" +
                "On turn:             " + (players.current() == players.get(WHITE));

        String blackLabelText =
                "Placing GIPF pieces? " + players.get(BLACK).isPlacingGipfPieces + "\n" +
                "Pieces left:         " + players.get(BLACK).reserve + "\n" +
                "On turn:             " + (players.current() == players.get(BLACK));

        whiteLabel.setText(whiteLabelText);
        blackLabel.setText(blackLabelText);
    }
}
