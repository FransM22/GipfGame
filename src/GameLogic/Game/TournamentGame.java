package GameLogic.Game;

import GameLogic.PieceType;
import GameLogic.Player;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 5-10-2015.
 */
public class TournamentGame extends Game {
    public TournamentGame() {
        super(GameType.tournament);
    }

    @Override
    void initializePlayers() {
        players.put(WHITE, new Player(WHITE, 18, true));
        players.put(BLACK, new Player(BLACK, 18, true));
    }

    @Override
    public boolean updateGameOverState() {
        long currentPlayersGipfPiecesOnBoard = gipfBoardState.getPieceMap()
                .values()
                .stream()
                .filter(piece ->
                        piece.getPieceType() == PieceType.GIPF && piece.getPieceColor() == getCurrentPlayer().pieceColor)
                .count();

        if (getWinningPlayer() == null) {
            if (getCurrentPlayer().reserve == 0 || (currentPlayersGipfPiecesOnBoard == 0 && getCurrentPlayer().hasPlacedGipfPieces)) {
                setWinningPlayer(getCurrentPlayer());
                return true;
            }
            else {
                return false;
            }
        }

        return true;
    }
}
