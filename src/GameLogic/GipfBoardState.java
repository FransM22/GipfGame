package GameLogic;

import AI.BoardStateProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game.
 */
public class GipfBoardState implements Serializable {
    public final BoardStateProperties boardStateProperties;
    private final HashMap<Position, Piece> pieceMap;
    public PlayersInGame players;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoardState() {
        // Initialize the lists
        this.pieceMap = new HashMap<>();
        this.players = new PlayersInGame();
        this.boardStateProperties = new BoardStateProperties(this);
    }

    /**
     * Initialize a new Gipf board, with the same pieces on the same locations as an old board.
     *
     * @param old board with pieces that should be copied
     */
    public GipfBoardState(GipfBoardState old) {
        this.pieceMap = new HashMap<>(old.pieceMap);
        this.players = new PlayersInGame(old.players);
        this.boardStateProperties = new BoardStateProperties(this);
    }

    public Map<Position, Piece> getPieceMap() {
        return pieceMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GipfBoardState)) return false;

        GipfBoardState that = (GipfBoardState) o;

        if (!(pieceMap.keySet().size() == that.pieceMap.keySet().size() && pieceMap.values().containsAll(that.pieceMap.values())))
            return false;
        return players.equals(that.players);

    }

    @Override
    public int hashCode() {
        int result = pieceMap.hashCode();
        result = 31 * result + players.hashCode();
        return result;
    }
}
