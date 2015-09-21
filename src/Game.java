import GameLogic.GipfBoard;
import GameLogic.Move;
import GameLogic.Position;

/**
 * The Game class keeps track of the running game.
 *
 * Created by frans on 7-9-2015.
 */
class Game {
    public static void main(String argv[]) {
        GipfBoard gipfBoard = new GipfBoard();

        printBoard(gipfBoard);

        System.out.println("Adding piece");
        Move m = new Move(
                GipfBoard.Piece.WHITE_GIPF,
                new Position('a', 5),
                Move.Direction.SOUTH_EAST
        );

        Move m2 = new Move(
                GipfBoard.Piece.BLACK_GIPF,
                new Position('a', 5),
                Move.Direction.SOUTH_EAST
        );

        gipfBoard.setPiece(new Position('e', 5), GipfBoard.Piece.WHITE_SINGLE);

        gipfBoard.applyMove(m);
        printBoard(gipfBoard);
        gipfBoard.applyMove(m2);
        printBoard(gipfBoard);
        gipfBoard.applyMove(m2);
        printBoard(gipfBoard);

        System.out.println("Gipf game started");
    }

    private static void printBoard(GipfBoard gb) {
        System.out.println(gb.getPieceMap());
    }
}
