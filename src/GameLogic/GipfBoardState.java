package GameLogic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game.
 *
 */
public class GipfBoardState implements Serializable {
    private HashMap<Position, Piece> pieceMap;

    public PlayersInGame players;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoardState() {
        // Initialize the lists
        pieceMap = new HashMap<>();
        this.players = new PlayersInGame();
    }

    /**
     * Initialize a new Gipf board, with the same pieces on the same locations as an old board.
     *
     * @param old board with pieces that should be copied
     * @param players
     */
    public GipfBoardState(GipfBoardState old, PlayersInGame players) {
        this.pieceMap = new HashMap<>(old.pieceMap);
        this.players = new PlayersInGame(players);
    }

    public Map<Position, Piece> getPieceMap() {
        return pieceMap;
    }

}
