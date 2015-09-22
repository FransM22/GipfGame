package GUI.GipfBoardComponent;

import GameLogic.Position;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * This class contains code that is run in a separate thread and controls the updating of hover positions. It is ran in
 * a different thread to make sure that the UI is not locked when the player hovers over positions
 * <p/>
 * Created by frans on 22-9-2015.
 */
public class HoverPositionUpdater implements Runnable {
    private final int hoverUpdateIntervalMs = 100;                                  // The interval in ms of updating the position over which is being hovered

    GipfBoardComponent gipfBoardComponent;
    private Position previousPosition = null;

    HoverPositionUpdater(GipfBoardComponent gipfBoardComponent) {
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
            PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
            Position newHoverPosition = positionHelper.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
            if (newHoverPosition != previousPosition) {
                if (gipfBoardComponent.game.isPositionOnBoard(newHoverPosition)) {
                    if (gipfBoardComponent.selectablePositions.contains(newHoverPosition)) {
                        gipfBoardComponent.currentHoverPosition = positionHelper.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
                        previousPosition = gipfBoardComponent.currentHoverPosition;
                        gipfBoardComponent.selectedMoveToPosition = null;
                    } else if (gipfBoardComponent.selectedPosition != null && gipfBoardComponent.moveToPositions.contains(newHoverPosition)) {
                        gipfBoardComponent.currentHoverPosition = positionHelper.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
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
