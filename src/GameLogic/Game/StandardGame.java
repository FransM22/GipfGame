package GameLogic.Game;

import GameLogic.Piece;
import GameLogic.Player;
import GameLogic.Position;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 5-10-2015.
 */
public class StandardGame extends Game {
    public StandardGame() {
        super(GameType.standard);
    }

    @Override
    void initializePlayers() {
        players.put(WHITE, new Player(WHITE, 12, false));
        players.put(BLACK, new Player(BLACK, 12, false));
    }

    @Override
    void initializeBoard() {
        super.initializeBoard();

        gipfBoardState.getPieceMap().put(new Position('b', 5), Piece.WHITE_GIPF);
        gipfBoardState.getPieceMap().put(new Position('e', 2), Piece.WHITE_GIPF);
        gipfBoardState.getPieceMap().put(new Position('h', 5), Piece.WHITE_GIPF);

        gipfBoardState.getPieceMap().put(new Position('b', 2), Piece.BLACK_GIPF);
        gipfBoardState.getPieceMap().put(new Position('e', 8), Piece.BLACK_GIPF);
        gipfBoardState.getPieceMap().put(new Position('h', 2), Piece.BLACK_GIPF);
    }
}
