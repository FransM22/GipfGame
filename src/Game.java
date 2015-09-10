import GameLogic.GipfBoard;
import GameLogic.Move;

/**
 * Created by frans on 7-9-2015.
 */
public class Game {
    public static void main(String argv[]) {
        GipfBoard gipfBoard = new GipfBoard();

        printBoard(gipfBoard);

        System.out.println("Adding piece");
        Move m = new Move(
                GipfBoard.Piece.WHITE_GIPF,
                new GipfBoard.Position('a', 5),
                new GipfBoard.Position('b', 5)
        );

        printBoard(gipfBoard);

        System.out.println("Gipf game started");
    }

    private static void printBoard(GipfBoard gb) {
        System.out.println(gb.getRow('a'));
        System.out.println(gb.getRow('b'));
        System.out.println(gb.getRow('c'));
        System.out.println(gb.getRow('d'));
        System.out.println(gb.getRow('e'));
        System.out.println(gb.getRow('f'));
        System.out.println(gb.getRow('g'));
        System.out.println(gb.getRow('h'));
        System.out.println(gb.getRow('i'));
    }
}
