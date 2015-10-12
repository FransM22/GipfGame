package GameLogic.Game;

import GameLogic.Piece;
import GameLogic.PieceType;
import GameLogic.Position;

/**
 * Created by frans on 5-10-2015.
 */
public class StandardGame extends Game {
    public StandardGame() {
        super(GameType.standard);
    }

    @Override
    void initializePlayers() {
        super.initializePlayers();

        players.values().stream()
                .forEach(player -> {
                    player.setReserve(12);
                    player.setHasPlacedGipfPieces(true);
                    player.setIsPlacingGipfPieces(false);
                    player.setHasPlacedNormalPieces(true);
                });
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
                        piece.getPieceType() == PieceType.GIPF && piece.getPieceColor() == players.current().pieceColor)
                .count();

        if (players.winner() == null) {
            if (players.current().reserve == 0 || currentPlayersGipfPiecesOnBoard == 0) {
                players.makeCurrentPlayerWinner();
                return true;
            }
            else {
                return false;
            }
        }

        return true;
    }
}
