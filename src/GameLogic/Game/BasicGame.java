package GameLogic.Game;

import GameLogic.Piece;
import GameLogic.Position;

/**
 * Created by frans on 5-10-2015.
 */
public class BasicGame extends Game {
    public BasicGame() {
        super(GameType.basic);
    }

    @Override
    void initializePlayers() {
        super.initializePlayers();

        players.values().stream()
                .forEach(player -> {
                    player.setReserve(12);                  // Set the reserve of each player to 12
                    player.setIsPlacingGipfPieces(false);
                    player.setHasPlacedNormalPieces(true);
                });
    }

    @Override
    void initializeBoard() {
        super.initializeBoard();

        gipfBoardState.getPieceMap().put(new Position('b', 5), Piece.WHITE_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('e', 2), Piece.WHITE_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('h', 5), Piece.WHITE_SINGLE);

        gipfBoardState.getPieceMap().put(new Position('b', 2), Piece.BLACK_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('e', 8), Piece.BLACK_SINGLE);
        gipfBoardState.getPieceMap().put(new Position('h', 2), Piece.BLACK_SINGLE);
    }

    @Override
    public boolean getGameOverState() {
        return players.current().reserve == 0;
    }
}
