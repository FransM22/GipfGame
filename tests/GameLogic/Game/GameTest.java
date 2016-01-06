package GameLogic.Game;

import AI.Players.RingPlayer;
import GameLogic.Direction;
import GameLogic.Move;
import GameLogic.Piece;
import GameLogic.Position;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frans on 4-1-2016.
 */
public class GameTest {
    Game basicGame;

    @BeforeMethod(groups = {"moveTests"})
    public void initializeBoard() throws Exception {
        System.out.print("[Initializing board] ");
        basicGame = new BasicGame();
    }

    @Test(groups = {"moveTests"})
    public void testApplyMove1() throws Exception {
        Move moveToBeTested = new Move(
                Piece.WHITE_SINGLE,
                new Position('e', 1),
                Direction.NORTH
        );
        basicGame.applyMove(moveToBeTested);

        System.out.println("testApplyMove1(): applyMove(" + moveToBeTested + ")");
        Game temporaryGame = new BasicGame();
        Map<Position, Piece> expectedPieceMap = new HashMap<>(temporaryGame.gipfBoardState.getPieceMap());
        expectedPieceMap.put(new Position('e', 3), temporaryGame.getGipfBoardState().getPieceMap().get(new Position('e', 2)));
        expectedPieceMap.put(new Position('e', 2), Piece.WHITE_SINGLE);

        Assert.assertEquals(
                basicGame.getGipfBoardState().getPieceMap(),
                expectedPieceMap
        );
    }

    @Test(groups = {"moveTests"})
    public void testApplyMove2() throws Exception {
        Move moveToBeTested = new Move(
                Piece.BLACK_GIPF,
                new Position('b', 6),
                Direction.SOUTH_EAST
        );
        basicGame.applyMove(moveToBeTested);

        System.out.println("testApplyMove2(): applyMove(" + moveToBeTested + ")");
        Game temporaryGame = new BasicGame();
        Map<Position, Piece> expectedPieceMap = new HashMap<>(temporaryGame.gipfBoardState.getPieceMap());
        expectedPieceMap.put(new Position('c', 6), Piece.BLACK_GIPF);

        Assert.assertEquals(
                basicGame.getGipfBoardState().getPieceMap(),
                expectedPieceMap
        );
    }

    @Test(groups = {"moveTests"})
    public void testApplyMove3() throws Exception {
        Move whiteMoveToBeTested = new Move(
                Piece.WHITE_SINGLE,
                new Position('a', 5),
                Direction.SOUTH_EAST
        );
        Move blackMoveToBeTested = new Move(
                Piece.BLACK_SINGLE,
                new Position('i', 1),
                Direction.NORTH_WEST
        );

        for (int i = 0; i < 2; i++) {
            basicGame.applyMove(whiteMoveToBeTested);
            basicGame.applyMove(blackMoveToBeTested);
        }
        basicGame.applyMove(whiteMoveToBeTested);   // Apply this move one more time

        System.out.println("testApplyMove3():\n" +
                "   3 * applyMove(" + whiteMoveToBeTested + ")\n" +
                " + 2 * applyMove(" + blackMoveToBeTested + ")");
        Game temporaryGame = new BasicGame();
        Map<Position, Piece> expectedPieceMap = new HashMap<>(temporaryGame.gipfBoardState.getPieceMap());
        expectedPieceMap.remove(new Position('b', 5));  // The entire diagonal will be cleared
        expectedPieceMap.remove(new Position('h', 2));  // ...


        Assert.assertEquals(
                basicGame.getGipfBoardState().getPieceMap(),
                expectedPieceMap
        );
    }

    @Test(groups = {"ringPlayerTests"})
    public void testRingplayer1() throws Exception {
        System.out.println("ringPlayer1() -- Lets the ringplayer create a vertical line e2-e8. Should be removed.");

        Game basicGame = new BasicGame();
        basicGame.whitePlayer = new RingPlayer();
        basicGame.blackPlayer = new RingPlayer();

        // First move white
        basicGame.applyMove(new Move(
                Piece.WHITE_SINGLE,
                new Position('e', 1),
                Direction.NORTH)
        );

        // First move black
        basicGame.applyMove(new Move(
                Piece.BLACK_SINGLE,
                new Position('e', 9),
                Direction.SOUTH)
        );

        for (int i = 0; i < 3; i++) {
            basicGame.applyCurrentPlayerMove();
        }

        // After 5 moves the ring player must have cleared exactly one row.
        // this can be checked by counting the remaining number of pieces
        Map<Position, Piece> expectedPieceMap = new HashMap<>();
        expectedPieceMap.put(new Position('b', 2), Piece.BLACK_SINGLE);
        expectedPieceMap.put(new Position('h', 2), Piece.BLACK_SINGLE);
        expectedPieceMap.put(new Position('b', 5), Piece.WHITE_SINGLE);
        expectedPieceMap.put(new Position('h', 5), Piece.WHITE_SINGLE);

        Assert.assertEquals(
                basicGame.getGipfBoardState().getPieceMap(),
                expectedPieceMap
                );
    }

    @Test(groups = {"ringPlayerTests"})
    public void testRingplayer2() throws Exception {
        System.out.println("ringPlayer2() -- Lets the ringplayer create two segments. One should be removed.");

        Game basicGame = new BasicGame();
        basicGame.whitePlayer = new RingPlayer();
        basicGame.blackPlayer = new RingPlayer();

        // First move white
        basicGame.applyMove(new Move(
                Piece.WHITE_SINGLE,
                new Position('i', 5),
                Direction.SOUTH_WEST)
        );

        // First move black
        basicGame.applyMove(new Move(
                Piece.BLACK_SINGLE,
                new Position('e', 9),
                Direction.SOUTH)
        );

        for (int i = 0; i < 3; i++) {
            basicGame.applyCurrentPlayerMove();
        }

        // After 5 moves the ring player must have cleared exactly one row.
        // this can be checked by counting the remaining number of pieces
        Map<Position, Piece> expectedPieceMap = new HashMap<>();
        expectedPieceMap.put(new Position('b', 2), Piece.BLACK_SINGLE);
        expectedPieceMap.put(new Position('h', 2), Piece.BLACK_SINGLE);
        expectedPieceMap.put(new Position('e', 6), Piece.BLACK_SINGLE);
        expectedPieceMap.put(new Position('e', 7), Piece.BLACK_SINGLE);
        expectedPieceMap.put(new Position('e', 8), Piece.BLACK_SINGLE);

        expectedPieceMap.put(new Position('b', 5), Piece.WHITE_SINGLE);
        expectedPieceMap.put(new Position('e', 2), Piece.WHITE_SINGLE);

        Assert.assertEquals(
                basicGame.getGipfBoardState().getPieceMap(),
                expectedPieceMap
        );
    }
}