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
        gipfBoard.setPiece(new GipfBoard.Position('a', 3), GipfBoard.Piece.BLACK_GIPF);

        gipfBoard.applyMove(m);
        printBoard(gipfBoard);


        System.out.println("Gipf game started");
    }

    private static void printBoard(GipfBoard gb) {
        // This prints the board rotated. Should be fixed in the graphical version

        System.out.println("    " + gb.getCol('a'));
        System.out.println("   " + gb.getCol('b'));
        System.out.println("  " + gb.getCol('c'));
        System.out.println(" " + gb.getCol('d'));
        System.out.println("" + gb.getCol('e'));
        System.out.println(" " + gb.getCol('f'));
        System.out.println("  " + gb.getCol('g'));
        System.out.println("   " + gb.getCol('h'));
        System.out.println("    " + gb.getCol('i'));
    }
}
