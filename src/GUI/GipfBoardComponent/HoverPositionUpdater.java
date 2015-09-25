package GUI.GipfBoardComponent;

import GameLogic.Game;
import GameLogic.Move;
import GameLogic.Position;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class contains code that is run in a separate thread and controls the updating of hover positions. It is ran in
 * a different thread to make sure that the UI is not locked when the player hovers over positions
 * <p/>
 * Created by frans on 22-9-2015.
 */
class HoverPositionUpdater implements Runnable {
    private final GipfBoardComponent gipfBoardComponent;

    HoverPositionUpdater(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(UIval.get().hoverUpdateIntervalMs);
            } catch (InterruptedException e) {
                gipfBoardComponent.currentHoverPosition = null;
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

            Set<Position> selectablePositions = getMoveToPositionsForStartPosition(gipfBoardComponent.selectedPosition);


            if (gipfBoardComponent.game.isPositionOnBoard(newHoverPosition)) {
                if (getStartPositionsForMoves().contains(newHoverPosition)) {
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

    private Set<Position> getStartPositionsForMoves() {
        return gipfBoardComponent.game.getAllowedMoves()
                .stream()
                .map(Move::getStartingPosition)
                .collect(Collectors.toSet());
    }

    private Set<Position> getMoveToPositionsForStartPosition(Position position) {
        Game game = gipfBoardComponent.game;
        return game.getAllowedMoves()
                .stream()
                .filter(m -> m.getStartingPosition().equals(position))
                .map(move -> new Position(
                        move.getStartingPosition().getPosId() + game.getDeltaPos(move.getDirection())))
                .collect(Collectors.toSet());
    }
}

