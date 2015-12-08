package GameLogic.Game;

import GameLogic.*;

import java.util.TreeMap;

/**
 * Created by frans on 5-10-2015.
 */
public class StandardGame extends Game {
    public StandardGame() {
        super();
    }

    @Override
    protected PlayersInGame initializePlayers() {
        PlayersInGame playersInGame = super.initializePlayers();

        for (PlayersInGame.Player player : playersInGame) {
            player.reserve = 12;
            player.setHasPlacedGipfPieces(true);
            player.setIsPlacingGipfPieces(false);
            player.setHasPlacedNormalPieces(true);
        }
        return playersInGame;
    }

    @Override
    protected TreeMap<Position, Piece> initializePieceMap() {
        TreeMap<Position, Piece> piecemap = super.initializePieceMap();

        piecemap.put(new Position('b', 5), Piece.WHITE_GIPF);
        piecemap.put(new Position('e', 2), Piece.WHITE_GIPF);
        piecemap.put(new Position('h', 5), Piece.WHITE_GIPF);

        piecemap.put(new Position('b', 2), Piece.BLACK_GIPF);
        piecemap.put(new Position('e', 8), Piece.BLACK_GIPF);
        piecemap.put(new Position('h', 2), Piece.BLACK_GIPF);

        return piecemap;
    }

    @Override
    public boolean getGameOverState(GipfBoardState gipfBoardState) {
        long currentPlayersGipfPiecesOnBoard = gipfBoardState.getPieceMap()
                .values()
                .stream()
                .filter(piece ->
                        piece.getPieceType() == PieceType.GIPF && piece.getPieceColor() == gipfBoardState.players.current().pieceColor)
                .count();

        if (gipfBoardState.players.winner() == null) {
            if (gipfBoardState.players.current().reserve == 0 || currentPlayersGipfPiecesOnBoard == 0) {
                gipfBoardState.players.makeCurrentPlayerWinner();
                return true;
            } else {
                return false;
            }
        }

        return true;
    }
}
