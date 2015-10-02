package GameLogic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game.
 *
 */
public class GipfBoard {
    private Map<Position, Game.Piece> pieceMap;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoard() {
        // Initialize the lists
        pieceMap = new HashMap<>();
    }

    /**
     * Initialize a new Gipf board, with the same pieces on the same locations as an old board.
     *
     * @param old board with pieces that should be copied
     */
    public GipfBoard(GipfBoard old) {
        pieceMap = new HashMap<>(old.pieceMap);
    }

    public Map<Position, Game.Piece> getPieceMap() {
        return pieceMap;
    }

}
