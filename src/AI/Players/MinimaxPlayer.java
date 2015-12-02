package AI.Players;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Optional;
import java.util.TreeMap;

/**
 * Created by frans on 1-12-2015.
 */
public class MinimaxPlayer extends ComputerPlayer {

    public MinimaxPlayer() {
        maxDepth = Optional.of(2);
    }

    @Override
    public Move apply(GipfBoardState gipfBoardState) {
        Game game = new BasicGame();
        game.loadState(gipfBoardState);


        // Using a treemap instead of a hashmap, because treemaps automatically sort their elements (in this case doubles)
        TreeMap<Integer, Move> moveGipfBoardStateMap = new TreeMap<>();
        for (Move move : game.getAllowedMoves()) {
            Game temporaryGame = new BasicGame();
            temporaryGame.loadState(gipfBoardState);
            temporaryGame.applyMove(move);

            // Sorts all board states based on heuristicRandomValue
            int minmaxvalue = temporaryGame.getGipfBoardState().boardStateProperties.minMaxValue;

            // White is the minimizing player, so if the current player is black, we flip the values
            if (temporaryGame.getGipfBoardState().players.current().pieceColor == PieceColor.BLACK) {
                minmaxvalue *= -1;
            }

            // We don't want to include values that are as of yet undetermined
            if (minmaxvalue != 0) {
                moveGipfBoardStateMap.put(minmaxvalue, move);
            }
        }

        if (moveGipfBoardStateMap.size() >= 1) {
            return moveGipfBoardStateMap.firstEntry().getValue();
        }
        return null;
    }
}
