package GameLogic;

import AI.BoardStateProperties;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game.
 */
public class GipfBoardState implements Serializable {
    public GipfBoardState parent;
    public final BoardStateProperties boardStateProperties;
    private final HashMap<Position, Piece> pieceMap;
    public PlayersInGame players;
    public Map<Move, GipfBoardState> exploredChildren;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoardState() {
        // Initialize the lists
        this.pieceMap = new HashMap<>();
        this.players = new PlayersInGame();
        exploredChildren = new HashMap<>();

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
        exploredChildren = new HashMap<>();

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

    public void exploreAllChildren() {
        List<Move> unexploredChildren = getUnexploredChildren();

        unexploredChildren.parallelStream().forEach(move -> {
            exploreChild(move);
        });
    }

    public void exploreChild(Move m) {
        Game temporaryGame = new BasicGame();
        temporaryGame.loadState(this);
        temporaryGame.applyMove(m);
        temporaryGame.getGipfBoardState().parent = this;

        exploredChildren.put(m, temporaryGame.getGipfBoardState());
    }

    public List<Move> getUnexploredChildren() {
        Game game = new BasicGame();
        game.loadState(this);
        return game.getAllowedMoves()
                .stream()
                .filter(move -> {
                    return !exploredChildren.keySet().contains(move);
                })
                .collect(toList());
    }
}
