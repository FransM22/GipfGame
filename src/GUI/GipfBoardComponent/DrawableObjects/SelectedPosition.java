package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;

import java.awt.*;

/**
 * Created by frans on 22-9-2015.
 */
public class SelectedPosition extends DrawableObject {
    public SelectedPosition(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        if (gipfBoardComponent.selectedPosition != null) {
            primitiveShapeHelper.centerCircleOn(positionHelper.positionToScreenX(gipfBoardComponent.selectedPosition), positionHelper.positionToScreenY(gipfBoardComponent.selectedPosition), UIval.get().pieceSize, UIval.get().whiteSingleColor, UIval.get().singlePieceBorderColor, UIval.get().hoverPositionStroke);
        }
    }
}
