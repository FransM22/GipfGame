import GameLogic.Direction;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.Position;

/**
 * The GipfGame class keeps track of the running game.
 * <p/>
 * Created by frans on 7-9-2015.
 */
class GipfGame {
    public static void main(String argv[]) {
        Game game = new BasicGame();
        GipfBoardState gipfBoardState = game.getGipfBoardState();

        printBoard(gipfBoardState);

        System.out.println("Adding piece");
        Move m = new Move(
                Game.Piece.WHITE_GIPF,
                new Position('a', 5),
                Direction.SOUTH_EAST
        );

        Move m2 = new Move(
                Game.Piece.BLACK_GIPF,
                new Position('a', 5),
                Direction.SOUTH_EAST
        );

        game.setPiece(game.getGipfBoardState(), new Position('e', 5), Game.Piece.WHITE_SINGLE);

        game.applyMove(m);
        printBoard(gipfBoardState);
        game.applyMove(m2);
        printBoard(gipfBoardState);
        game.applyMove(m2);
        printBoard(gipfBoardState);

        System.out.println("Gipf game started");
    }

    private static void printBoard(GipfBoardState gb) {
        System.out.println(gb.getPieceMap());
    }
}
