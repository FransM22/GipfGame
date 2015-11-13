package AI;

import GameLogic.GipfBoardState;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 12-11-2015.
 */
public class BoardStateProperties {
    private GipfBoardState gipfBoardState;
    public double heuristicRandomValue;
    public int heuristicBlackMinusWhitePieces;
    public int minMaxValue;

    public BoardStateProperties(GipfBoardState gipfBoardState) {
        this.gipfBoardState = gipfBoardState;
    }

    public void update() {
        this.heuristicRandomValue = new AssignRandomValue().apply(gipfBoardState);
        this.heuristicBlackMinusWhitePieces = gipfBoardState.players.get(BLACK).reserve - gipfBoardState.players.get(WHITE).reserve;

        this.minMaxValue = new AssignMinMaxValue().apply(gipfBoardState);
    }
}
