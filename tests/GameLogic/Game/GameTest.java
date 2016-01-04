package GameLogic.Game;

import GameLogic.Direction;
import GameLogic.Move;
import GameLogic.Piece;
import GameLogic.Position;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frans on 4-1-2016.
 */
public class GameTest {
    Game basicGame;

    @BeforeTest
    public void initializeBoard() {
        System.out.println("Initializing board...");
        basicGame = new BasicGame();
    }

    @Test
    public void testApplyMove() throws Exception {
        Move moveToBeTested = new Move(
                Piece.WHITE_SINGLE,
                new Position('e', 1),
                Direction.NORTH
        );
        basicGame.applyMove(moveToBeTested);

        System.out.println("testApplyMove(): applyMove(" + moveToBeTested + ")");
        Game temporaryGame = new BasicGame();
        Map<Position, Piece> expectedPieceMap = new HashMap<>(temporaryGame.gipfBoardState.getPieceMap());
        expectedPieceMap.put(new Position('e', 3), temporaryGame.getGipfBoardState().getPieceMap().get(new Position('e', 2)));
        expectedPieceMap.put(new Position('e', 2), Piece.WHITE_SINGLE);

        Assert.assertEquals(
                basicGame.getGipfBoardState().getPieceMap(),
                expectedPieceMap
        );
    }
}