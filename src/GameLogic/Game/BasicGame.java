package GameLogic.Game;

/**
 * Created by frans on 5-10-2015.
 */
public class BasicGame extends Game {
    public BasicGame() {
        super(GameType.basic);
    }

    @Override
    void initializePlayers() {
        whitePlayer = new Player(PieceColor.WHITE, 15, false);
        blackPlayer = new Player(PieceColor.BLACK, 15, false);
    }
}
