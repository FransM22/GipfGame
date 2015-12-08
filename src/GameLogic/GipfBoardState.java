package GameLogic;

import AI.BoardStateProperties;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;

import java.io.Serializable;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by frans on 8-9-2015.
 * This class represents the board that is used in the game. All fields storing properties of the board itself are final,
 * so have to be recreated when a new child state is used. This is done to avoid problems with multiple GipfBoardStates
 * belonging to the same game state. The volatile values (such as heuristics) are stored in the BoardStateProperties
 * class.
 */
public class GipfBoardState implements Serializable {
    public final GipfBoardState parent;
    public final BoardStateProperties boardStateProperties;
    public final PlayersInGame players;
    private final TreeMap<Position, Piece> pieceMap;
    public Map<Move, GipfBoardState> exploredChildren;

    /**
     * Initialize an empty Gipf board
     */
    public GipfBoardState() {
        // Initialize the lists
        this.pieceMap = new TreeMap<>();  // A treemap appears to be a little bit faster than a hashmap
        this.players = new PlayersInGame();
        exploredChildren = new HashMap<>(50);

        parent = null;
        this.boardStateProperties = new BoardStateProperties(this);
    }

    public GipfBoardState(GipfBoardState parent, Map<Position, Piece> pieceMap, PlayersInGame players) {
        // Initialize the lists
        this.pieceMap = new TreeMap<>(pieceMap);
        this.players = players;
        exploredChildren = new HashMap<>(50);

        this.parent = parent;
        this.boardStateProperties = new BoardStateProperties(this);
    }

    public Map<Position, Piece> getPieceMap() {
        return Collections.unmodifiableMap(pieceMap);
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
