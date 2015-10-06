package GameLogic.Game;

import GameLogic.Piece;
import GameLogic.PieceColor;
import GameLogic.Player;
import GameLogic.Position;

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

    @Override
    void initializeBoard() {
        super.initializeBoard();

        gipfBoardState.getPieceMap().put(new Position('b', 5), Piece.WHITE_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('e', 2), Piece.WHITE_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('h', 5), Piece.WHITE_SINGLE);

        gipfBoardState.getPieceMap().put(new Position('b', 2), Piece.BLACK_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('e', 8), Piece.BLACK_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('h', 2), Piece.BLACK_SINGLE);
    }
}
