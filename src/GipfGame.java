import GameLogic.*;

/**
 * The GipfGame class keeps track of the running game.
 * <p/>
 * Created by frans on 7-9-2015.
 */
class GipfGame {
    public static void main(String argv[]) {
        Game game = new Game();
        GipfBoard gipfBoard = game.getGipfBoard();

        printBoard(gipfBoard);

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

        game.setPiece(new Position('e', 5), Game.Piece.WHITE_SINGLE);

        game.applyMove(m);
        printBoard(gipfBoard);
        game.applyMove(m2);
        printBoard(gipfBoard);
        game.applyMove(m2);
        printBoard(gipfBoard);

        System.out.println("Gipf game started");
    }

    private static void printBoard(GipfBoard gb) {
        System.out.println(gb.getPieceMap());
    }
}
