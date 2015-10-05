package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

/**
 * Created by frans on 5-10-2015.
 */
public class SelectedRemovePositions extends DrawableObject {
    Set<Position> selectedRemovePositions;

    public SelectedRemovePositions(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
        selectedRemovePositions = gipfBoardComponent.selectedRemovePositions;

    }

    @Override
    public void draw() {
        g2.setColor(UIval.get().removedPieceSelectionColor);
        g2.setStroke(UIval.get().removedPieceSelectionStroke);
        int lineSizeHalf = 10;

        for (Position position : selectedRemovePositions) {
            int screenX = positionHelper.positionToScreenX(position);
            int screenY = positionHelper.positionToScreenY(position);

            g2.drawLine(screenX - lineSizeHalf, screenY - lineSizeHalf, screenX + lineSizeHalf, screenY + lineSizeHalf);
            g2.drawLine(screenX + lineSizeHalf, screenY - lineSizeHalf, screenX - lineSizeHalf, screenY + lineSizeHalf);
        }
    }
}
