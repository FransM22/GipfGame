package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Position;

import java.awt.*;

/**
 * Created by frans on 23-9-2015.
 */
public class SelectedMoveToArrow extends DrawableObject {

    public SelectedMoveToArrow(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        Position selectedMoveToPosition = gipfBoardComponent.selectedMoveToPosition;
        Position selectedPosition = gipfBoardComponent.selectedPosition;

        if (selectedMoveToPosition != null) {
            // Get the allowed positions from here
            g2.setColor(UIval.get().moveToArrowColor);
            g2.setStroke(UIval.get().moveToArrowStroke);
            g2.drawLine(
                    positionHelper.positionToScreenX(selectedPosition),
                    positionHelper.positionToScreenY(selectedPosition),
                    positionHelper.positionToScreenX(selectedMoveToPosition),
                    positionHelper.positionToScreenY(selectedMoveToPosition)
            );
        }
    }
}
