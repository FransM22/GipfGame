package GameLogic.Game;

import GameLogic.GipfBoardState;
import GameLogic.Piece;
import GameLogic.PieceType;
import GameLogic.PlayersInGame;

/**
 * Created by frans on 5-10-2015.
 */
public class TournamentGame extends Game {
    public TournamentGame() {
        super();
    }

    @Override
    protected PlayersInGame initializePlayers() {
        PlayersInGame players = super.initializePlayers();

        for (PlayersInGame.Player player : players) {
            player.reserve = 18;
            player.setMustStartWithGipfPieces(true);
        }

        return players;
    }

    @Override
    public boolean getGameOverState(GipfBoardState gipfBoardState) {
        long currentPlayersGipfPiecesOnBoard = gipfBoardState.getPieceMap().values().stream()
                .filter(piece ->
                        piece.equals(Piece.of(PieceType.GIPF, gipfBoardState.players.current().pieceColor)))
                .count();

        if (gipfBoardState.players.current().reserve == 0 || currentPlayersGipfPiecesOnBoard == 0) {
            gipfBoardState.players.makeCurrentPlayerWinner();
            return true;
        } else {
            return false;
        }
    }
}
