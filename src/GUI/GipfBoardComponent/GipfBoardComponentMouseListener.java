package GUI.GipfBoardComponent;

import GameLogic.Direction;
import GameLogic.Game;
import GameLogic.Move;
import GameLogic.Position;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class acts as the MouseListener for GipfBoardComponent. If no instance of this class is added as a mouse listener
 * to the component, the component will not react on mouse movements or clicks
 * <p/>
 * Created by frans on 22-9-2015.
 */
public class GipfBoardComponentMouseListener extends MouseAdapter {
    private final GipfBoardComponent gipfBoardComponent;
    private Thread hoverThread;

    public GipfBoardComponentMouseListener(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Act on mouse press, not on a click (press and release)
        int mouseX = e.getX();
        int mouseY = e.getY();

        PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
        Position selectedPosition = positionHelper.screenCoordinateToPosition(mouseX, mouseY);
        Game game = gipfBoardComponent.game;

        // Only allow to put pieces on selectable positions
        if (gipfBoardComponent.selectablePositions.contains(selectedPosition)) {
            gipfBoardComponent.selectedPosition = selectedPosition;

            gipfBoardComponent.repaint();
        } else if (gipfBoardComponent.selectedMoveToPosition != null) {
            int deltaPos = gipfBoardComponent.selectedMoveToPosition.getPosId() - gipfBoardComponent.selectedPosition.getPosId();
            Move currentMove = new Move(game.getCurrentPiece(), gipfBoardComponent.selectedPosition, Direction.getDirectionFromDeltaPos(deltaPos));
            game.applyMove(currentMove);
            gipfBoardComponent.selectedPosition = null;
            gipfBoardComponent.selectedMoveToPosition = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hoverThread = new Thread(new HoverPositionUpdater(gipfBoardComponent));
        hoverThread.start();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hoverThread.interrupt();
    }
}
