package GUI.GipfBoardComponent;

import GameLogic.Position;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class acts as the MouseListener for GipfBoardComponent. If no instance of this class is added as a mouse listener
 * to the component, the component will not react on mouse movements or clicks
 * <p/>
 * Created by frans on 22-9-2015.
 */
public class GipfBoardComponentMouseListener implements MouseListener {
    private final GipfBoardComponent gipfBoardComponent;
    private Thread hoverThread;

    public GipfBoardComponentMouseListener(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
        Position selectedPosition = positionHelper.screenCoordinateToPosition(e.getX(), e.getY());

        // Only allow to put pieces on selectable positions
        if (gipfBoardComponent.selectablePositions.contains(selectedPosition)) {
            gipfBoardComponent.selectedPosition = selectedPosition;

            gipfBoardComponent.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

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
