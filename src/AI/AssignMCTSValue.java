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
class AssignMCTSValue implements Function<GipfBoardState, Double> {
    private Random random = new Random();
    private int t = 0; //Total number simulations hence private

    @Override
    public Double apply(GipfBoardState startNodeBoardState) {

        Game game = new BasicGame();
        game.loadState(startNodeBoardState);

        for (int current_n = 1; current_n <= 3; current_n++) {
            int w = 0; //wi,  Number wins after current move
            double c = Math.sqrt(2); // Exploration parameter

            Game temporaryGame = new BasicGame();
            temporaryGame.loadState(startNodeBoardState);

            GipfBoardState copyOfState = new GipfBoardState(startNodeBoardState);
            if (winnerOfRandomGame(copyOfState) == WHITE) {
                w = 1;
            }

            temporaryGame.getGipfBoardState().boardStateProperties.mcts_n++;
            temporaryGame.getGipfBoardState().boardStateProperties.mcts_w += w;
            temporaryGame.getGipfBoardState().boardStateProperties.mctsDouble = temporaryGame.getGipfBoardState().boardStateProperties.mcts_w / (temporaryGame.getGipfBoardState().boardStateProperties.mcts_n + 0.00001);

            // update the parents recursively
            GipfBoardState currentParent = startNodeBoardState.parent;
            while (currentParent != null) {
                currentParent.boardStateProperties.mcts_n++;
                currentParent.boardStateProperties.mcts_w += w;
                currentParent.boardStateProperties.mctsDouble = currentParent.boardStateProperties.mcts_w / (currentParent.boardStateProperties.mcts_n + 0.00001);
                currentParent = currentParent.parent;
            }
        }

        return 0.0; // MCTSValue (current_w / current_n) + c * Math.sqrt(current_n / t); // This line gives a division by 0 error
    }

    private PieceColor winnerOfRandomGame(GipfBoardState gipfBoardState) {
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



