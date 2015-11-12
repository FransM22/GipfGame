package AI;

import GameLogic.GipfBoardState;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 12-11-2015.
 */
public class BoardStateProperties {
    public double heuristicRandomValue;
    public int heuristicBlackMinusWhitePieces;

    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.heuristicRandomValue = Math.random();
        this.heuristicBlackMinusWhitePieces = gipfBoardState.players.get(BLACK).reserve - gipfBoardState.players.get(WHITE).reserve;
    }
}
