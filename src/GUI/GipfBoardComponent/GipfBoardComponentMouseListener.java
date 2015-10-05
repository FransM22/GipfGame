package GUI.GipfBoardComponent;

import GUI.UIval;
import GameLogic.Direction;
import GameLogic.Game;
import GameLogic.Move;
import GameLogic.Position;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This class acts as the MouseListener for GipfBoardComponent. If no instance of this class is added as a mouse listener
 * to the component, the component will not react on mouse movements or clicks
 * <p/>
 * Created by frans on 22-9-2015.
 */
class GipfBoardComponentMouseListener extends MouseAdapter implements Runnable {
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
            if (selectedPosition.equals(gipfBoardComponent.selectedPosition)) {
                // Toggle gipf pieces
                this.gipfBoardComponent.game.getCurrentPlayer().toggleIsPlacingGipfPieces();
            }
            else {
                gipfBoardComponent.selectedPosition = selectedPosition;
            }
            gipfBoardComponent.repaint();
        } else if (gipfBoardComponent.selectedMoveToPosition != null) {
            int deltaPos = Position.getDeltaPos(gipfBoardComponent.selectedPosition, gipfBoardComponent.selectedMoveToPosition);
            Move currentMove = new Move(game.getCurrentPiece(), gipfBoardComponent.selectedPosition, Direction.getDirectionFromDeltaPos(deltaPos));
            game.applyMove(currentMove);
            gipfBoardComponent.selectedPosition = null;
            gipfBoardComponent.selectedMoveToPosition = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hoverThread = new Thread(this);
        hoverThread.start();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hoverThread.interrupt();
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(UIval.get().hoverUpdateIntervalMs);
            } catch (InterruptedException e) {
                gipfBoardComponent.currentHoverPosition = null;
                gipfBoardComponent.selectedMoveToPosition = null;

                gipfBoardComponent.repaint();

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

            Set<Position> selectablePositions = gipfBoardComponent.game.getMoveToPositionsForStartPosition(gipfBoardComponent.selectedPosition);


            if (gipfBoardComponent.game.isPositionOnBigBoard(newHoverPosition)) {
                if (gipfBoardComponent.game.getStartPositionsForMoves().contains(newHoverPosition)) {
                    // If the mouse hovers over a position on the border of the board
                    // select it
                    gipfBoardComponent.selectedMoveToPosition = null;
                    gipfBoardComponent.currentHoverPosition = newHoverPosition;
                } else if (selectablePositions.contains(newHoverPosition)) {
                    // If there is a position selected, and the mouse is hovering over a position where that piece can move to,
                    // clear the hover circle, and update the arrow indicating where the player can move
                    gipfBoardComponent.selectedMoveToPosition = newHoverPosition;
                    gipfBoardComponent.currentHoverPosition = newHoverPosition;
                } else {
                    // If the player is hovering over a position on the board, but it can't put a piece on it, or select it,
                    // the hover circle and the arrow indicating where the player can move are cleared
                    gipfBoardComponent.currentHoverPosition = null;
                    gipfBoardComponent.selectedMoveToPosition = null;
                }
            } else {
                // If the mouse is not hovering over a position on the board, clear the arrow and hover circle
                gipfBoardComponent.currentHoverPosition = null;
                gipfBoardComponent.selectedMoveToPosition = null;
            }
            gipfBoardComponent.repaint();
        }
    }
}
