package GameLogic.Game;

import GameLogic.Piece;
import GameLogic.PieceType;
import GameLogic.PlayersInGame;
import GameLogic.Position;

/**
 * Created by frans on 5-10-2015.
 */
public class StandardGame extends Game {
    public StandardGame() {
        super();
    }

    @Override
    void initializePlayers() {
        super.initializePlayers();

        for (PlayersInGame.Player player : gipfBoardState.players) {
            player.reserve = 12;
            player.setHasPlacedGipfPieces(true);
            player.setIsPlacingGipfPieces(false);
            player.setHasPlacedNormalPieces(true);
        }
    }

    @Override
    void initializeBoard() {
        super.initializeBoard();

        gipfBoardState.getPieceMap().put(new Position('b', 5), Piece.WHITE_GIPF);
        gipfBoardState.getPieceMap().put(new Position('e', 2), Piece.WHITE_GIPF);
        gipfBoardState.getPieceMap().put(new Position('h', 5), Piece.WHITE_GIPF);

        gipfBoardState.getPieceMap().put(new Position('b', 2), Piece.BLACK_GIPF);
        gipfBoardState.getPieceMap().put(new Position('e', 8), Piece.BLACK_GIPF);
        gipfBoardState.getPieceMap().put(new Position('h', 2), Piece.BLACK_GIPF);
    }

    @Override
    public boolean getGameOverState() {
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
