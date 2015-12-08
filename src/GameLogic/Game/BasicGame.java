package GameLogic.Game;

import GameLogic.GipfBoardState;
import GameLogic.Piece;
import GameLogic.PlayersInGame;
import GameLogic.Position;

import java.util.TreeMap;

/**
 * Created by frans on 5-10-2015.
 */
public class BasicGame extends Game {
    public BasicGame() {
        super();
    }

    @Override
    protected PlayersInGame initializePlayers() {
        PlayersInGame players = super.initializePlayers();

        for (PlayersInGame.Player player : players) {
            player.reserve = 12;                  // Set the reserve of each player to 12
            player.setIsPlacingGipfPieces(false);
            player.setHasPlacedNormalPieces(true);
        }

        return  players;
    }

    @Override
    protected TreeMap<Position, Piece> initializePieceMap() {
        TreeMap<Position, Piece> pieceMap = super.initializePieceMap();

        pieceMap.put(new Position('b', 5), Piece.WHITE_SINGLE);
        pieceMap.put(new Position('e', 2), Piece.WHITE_SINGLE);
        pieceMap.put(new Position('h', 5), Piece.WHITE_SINGLE);

        pieceMap.put(new Position('b', 2), Piece.BLACK_SINGLE);
        pieceMap.put(new Position('e', 8), Piece.BLACK_SINGLE);
        pieceMap.put(new Position('h', 2), Piece.BLACK_SINGLE);

        return pieceMap;
    }

    @Override
    public boolean getGameOverState(GipfBoardState gipfBoardState) {
        return gipfBoardState.players.current().reserve == 0;
    }
}
