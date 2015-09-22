package GUI;

import GameLogic.Position;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.TimeUnit;

/**
 * This class acts as the MouseListener for GipfBoardComponent. If no instance of this class is added as a mouse listener
 * to the component, the component will not react on mouse movements or clicks
 * <p/>
 * Created by frans on 22-9-2015.
 */
public class GipfBoardComponentMouseListener implements MouseListener {
    private final int hoverUpdateIntervalMs = 100;                                  // The interval in ms of updating the position over which is being hovered
    GipfBoardComponent gipfBoardComponent;
    private Thread hoverThread;

    GipfBoardComponentMouseListener(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Position selectedPosition = gipfBoardComponent.screenCoordinateToPosition(e.getX(), e.getY());

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
        hoverThread = new Thread(new UpdateHoverPosition(gipfBoardComponent));
        hoverThread.start();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hoverThread.interrupt();

        gipfBoardComponent.currentHoverPosition = null;
        gipfBoardComponent.repaint();
    }

    /**
     * This class contains code that is run in a separate thread and controls the updating of hover positions. It is ran in
     * a different thread to make sure that the UI is not locked when the player hovers over positions
     */
    private class UpdateHoverPosition implements Runnable {
        GipfBoardComponent gipfBoardComponent;
        private Position previousPosition = null;

        UpdateHoverPosition(GipfBoardComponent gipfBoardComponent) {
            this.gipfBoardComponent = gipfBoardComponent;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(hoverUpdateIntervalMs);
                } catch (InterruptedException e) {
                    // Interrupt the thread
                    break;
                }

                Point mouseLocation = MouseInfo.getPointerInfo().getLocation();                             // Get the mouse position relative to the screen
                Point componentPosition = gipfBoardComponent.getLocationOnScreen();                                            // Get the component position relative to the screen
                mouseLocation.translate((int) -componentPosition.getX(), (int) -componentPosition.getY()); // Calculate the mouse position relative to the component

                // Only update the position if the new position is different from the old position, and if the new
                // position is actually located on the board
                Position newHoverPosition = gipfBoardComponent.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
                if (newHoverPosition != previousPosition) {
                    if (gipfBoardComponent.game.isPositionOnBoard(newHoverPosition)) {
                        if (gipfBoardComponent.selectablePositions.contains(newHoverPosition)) {
                            gipfBoardComponent.currentHoverPosition = gipfBoardComponent.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
                            previousPosition = gipfBoardComponent.currentHoverPosition;
                            gipfBoardComponent.selectedMoveToPosition = null;
                        } else if (gipfBoardComponent.selectedPosition != null && gipfBoardComponent.moveToPositions.contains(newHoverPosition)) {
                            gipfBoardComponent.currentHoverPosition = gipfBoardComponent.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
                            gipfBoardComponent.selectedMoveToPosition = gipfBoardComponent.currentHoverPosition;
                            previousPosition = gipfBoardComponent.currentHoverPosition;
                        } else {
                            gipfBoardComponent.currentHoverPosition = null;
                        }
                    } else {
                        gipfBoardComponent.currentHoverPosition = null;
                        gipfBoardComponent.selectedMoveToPosition = null;
                    }
                    gipfBoardComponent.repaint();
                }
            }
        }
    }
}
