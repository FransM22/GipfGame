package GameLogic.Game;

import GameLogic.Piece;
import GameLogic.PieceType;

/**
 * Created by frans on 5-10-2015.
 */
public class TournamentGame extends Game {
    public TournamentGame() {
        super(GameType.tournament);
    }

    @Override
    void initializePlayers() {
        super.initializePlayers();

        players.values().stream()
                .forEach(player -> {
                    player.setReserve(18);
                    player.setMustStartWithGipfPieces(true);
                });
    }

    @Override
    public boolean getGameOverState() {
        long currentPlayersGipfPiecesOnBoard = gipfBoardState.getPieceMap().values().stream()
                .filter(piece ->
                        piece.equals(Piece.of(PieceType.GIPF, players.current().pieceColor)))
                .count();

        if (players.current().getReserve() == 0 || currentPlayersGipfPiecesOnBoard == 0) {
            players.makeCurrentPlayerWinner();
            return true;
        } else {
            return false;
        }
    }
}
