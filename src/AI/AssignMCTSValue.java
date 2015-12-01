package AI;

import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import static GameLogic.PieceColor.WHITE;
import static java.util.stream.Collectors.toList;

/**
 * Created by Dingding
 */
public class AssignMCTSValue implements Function<GipfBoardState, Double> {
    Random random = new Random();
    private int t = 0; //Total number simulations hence private

    @Override
    public Double apply(GipfBoardState startNodeBoardState) {

        Game game = new BasicGame();
        game.loadState(startNodeBoardState);

        for (int current_n = 1; current_n <= 2; current_n++) {
            int w = 0; //wi,  Number wins after current move
            double c = Math.sqrt(2); // Exploration parameter
            //double MCTSValue = (current_w / current_n) + c * Math.sqrt(current_n / t); // This line gives a division by 0 error

            Game temporaryGame = new BasicGame();
            temporaryGame.loadState(startNodeBoardState);

            GipfBoardState copyOfState = new GipfBoardState(startNodeBoardState);
            if (winnerOfRandomGame(copyOfState) == WHITE) {
                w = 1;
            }

            temporaryGame.getGipfBoardState().boardStateProperties.mcts_n++;
            temporaryGame.getGipfBoardState().boardStateProperties.mcts_w += w;

            // update the parents recursively
            GipfBoardState currentParent = startNodeBoardState.parent;
            while (currentParent != null) {
                currentParent.boardStateProperties.mcts_n++;
                currentParent.boardStateProperties.mcts_w += w;
                currentParent = currentParent.parent;
            }
        }

        return 0.; //MCTSValue;
    }

    public PieceColor winnerOfRandomGame(GipfBoardState gipfBoardState) {
        Game randomGame = new BasicGame();
        randomGame.loadState(gipfBoardState);

        while (randomGame.getGipfBoardState().players.winner() == null) {
            Set<Move> allowedMoves = randomGame.getAllowedMoves();
            int randomMoveId = random.nextInt(allowedMoves.size());
            randomGame.applyMove(allowedMoves.stream().collect(toList()).get(randomMoveId));
        }

        return randomGame.getGipfBoardState().players.winner().pieceColor;
    }
}



