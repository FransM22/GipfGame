package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;

/**
 * Created by frans on 22-9-2015.
 */
public class SelectedStartPosition extends Circle {
    public SelectedStartPosition(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Set<Position> selectedPositions) {
        super(g2,
                gipfBoardComponent,
                selectedPositions,
                UIval.get().pieceSize,
                gipfBoardComponent.getColorOfPlayer(gipfBoardComponent.game.players.current()),
                gipfBoardComponent.getBorderColorOfPlayer(gipfBoardComponent.game.players.current()),
                UIval.get().hoverPositionStroke);
    }
}
