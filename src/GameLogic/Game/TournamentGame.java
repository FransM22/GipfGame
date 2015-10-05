package GameLogic.Game;

import GameLogic.PieceColor;
import GameLogic.Player;

/**
 * Created by frans on 5-10-2015.
 */
public class TournamentGame extends Game {
    public TournamentGame() {
        super(GameType.tournament);
    }

    @Override
    void initializePlayers() {
        whitePlayer = new Player(PieceColor.WHITE, 18, true);
        blackPlayer = new Player(PieceColor.BLACK, 18, true);
    }
}
