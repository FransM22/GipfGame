package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by frans on 12-11-2015.
 */
public class ChooseRandomBoard {
    Random random;

    public Move ChooseRandomBoard(GipfBoardState gipfBoardState) {
        random = new Random(gipfBoardState.hashCode());

        Game game = new BasicGame(false);
        game.loadState(gipfBoardState);

        Set<Move> allowedMoves = game.getAllowedMoves();
        Iterator<Move> moveIterator = allowedMoves.iterator();

        int randomMove = random.nextInt(allowedMoves.size() - 1);
        for (int i = 0; i < randomMove; i++) {
            moveIterator.next();
        }

        return moveIterator.next();
    }
}
