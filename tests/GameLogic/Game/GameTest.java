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
        System.out.println("Initializing board...");
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

        System.out.println("testApplyMove3(): 3 * applyMove(" + whiteMoveToBeTested + ")\n" +
                           "                + 2 * applyMove(" + blackMoveToBeTested + ")");
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
        System.out.println("ringPlayerTests()");

        Game basicGame = new BasicGame();
        basicGame.whitePlayer = new RingPlayer();
        basicGame.blackPlayer = new RingPlayer();

//        basicGame.applyMove(basicGame);
        // TODO: We need to allow only a single move (or a fixed amount of moves) at a time in the automaticPlayThread (gameLoopRunnable)
    }
}