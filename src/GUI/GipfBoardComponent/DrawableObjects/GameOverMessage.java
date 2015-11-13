package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;

import java.awt.*;

/**
 * Created by frans on 2-10-2015.
 */
public class GameOverMessage extends DrawableObject {
    private final Font gameOverFont;

    public GameOverMessage(Graphics2D g2, Font gameOverFont, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
        this.gameOverFont = gameOverFont;
    }

    @Override
    public void draw() {
        if (gipfBoardComponent.game.getGipfBoardState().players.winner() != null) {
            g2.setFont(gameOverFont);
            g2.setColor(UIval.get().gameOverTextOutlineColor);
            g2.drawString(gipfBoardComponent.game.getGipfBoardState().players.winner().pieceColor + " won!", 52, 102);
            g2.setColor(UIval.get().gameOverTextColor);
            g2.drawString(gipfBoardComponent.game.getGipfBoardState().players.winner().pieceColor + " won!", 50, 100);
        }
    }
}
