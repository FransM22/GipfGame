package GUI2;

import GUI.GipfBoardComponent.GipfBoardComponent;
import javafx.scene.control.Label;

import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 10-11-2015.
 */
public class UpdateTreeTableViewSelection {
    private Label label;
    private GipfBoardComponent gipfBoardComponent;


    public UpdateTreeTableViewSelection(Label label, GipfBoardComponent gipfBoardComponent) {
        this.label = label;
        this.gipfBoardComponent = gipfBoardComponent;
    }

    public void updateDescriptionLabel() {
        String labelText = gipfBoardComponent.game.players.values().stream().map(
                player -> {
                    return player.pieceColor.toString() + ": " + player.reserve + "\n" +
                            " isPlacingGipfPieces: " + player.isPlacingGipfPieces + "\n" +
                            " hasPlacedNormalPieces: " + player.hasPlacedNormalPieces + "\n";
                }
        ).collect(toSet()).toString();
        label.setText(labelText);
    }
}
