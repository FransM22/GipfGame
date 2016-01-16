package AI;

import AI.Players.BlobPlayer;
import AI.Players.MCTSPlayer;
import AI.Players.RandomPlayer;
import Exceptions.GameEndException;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.GipfBoardState;
import GameLogic.Move;
import GameLogic.PieceColor;

import java.util.function.Function;

/**
 * Created by Dingding
 */
public class AssignPureMCTSValue implements Function<GipfBoardState, Double> {
    private double calculateUCT(GipfBoardState gipfBoardState, int t) {
        BoardStateProperties bsp = gipfBoardState.boardStateProperties;

        if (bsp.mcts_n == 0) {
            return Double.MAX_VALUE;
        }

        return (((double) bsp.mcts_w / (double) bsp.mcts_n) + Math.sqrt(2) * (Math.log(t) / bsp.mcts_n));
    }

    @Override
    public Double apply(GipfBoardState startNodeBoardState) {
        startNodeBoardState.exploreAllChildren();

        for (int current_t = 1; current_t <= 3; current_t++) {
            // Phase 1: Selection
            Move favourableMove = new MCTSPlayer().getMoveWithLowestHeuristicValue(startNodeBoardState, true);

            GipfBoardState outcomeOfFavourableMove = startNodeBoardState.exploredChildren.get(favourableMove);

            // Phase 2 & 3: Expansion & Simulation
            PieceColor winnerOfRandomGame = winnerOfRandomGame(outcomeOfFavourableMove);

            boolean white_won = (winnerOfRandomGame == PieceColor.WHITE);
            outcomeOfFavourableMove.boardStateProperties.mcts_w += white_won ? 1 : 0;      // w increases only if we won the random game
            outcomeOfFavourableMove.boardStateProperties.mcts_n += 1;          // We played a game, so n increases


            // Calculate the mcts value for all siblings
            final int finalCurrent_t = current_t;
            startNodeBoardState.exploredChildren.values().forEach(boardState -> boardState.boardStateProperties.mctsValue = calculateUCT(boardState, finalCurrent_t));
            outcomeOfFavourableMove.boardStateProperties.mctsValue = calculateUCT(outcomeOfFavourableMove, finalCurrent_t);

            // Phase 4: Backpropagation. update the parents recursively
            GipfBoardState currentParent = startNodeBoardState;
            while (currentParent != null) {
                currentParent.boardStateProperties.mcts_n++;
                currentParent.boardStateProperties.mcts_w += white_won ? 1 : 0;
                currentParent.boardStateProperties.mctsValue = calculateUCT(currentParent, current_t);
                currentParent = currentParent.parent;
            }
        }

        return 0.0;
    }

    private PieceColor winnerOfRandomGame(GipfBoardState gipfBoardState) {
        Game randomGame = new BasicGame();
        randomGame.blackPlayer = new RandomPlayer();
        randomGame.whitePlayer = new RandomPlayer();

        randomGame.loadState(gipfBoardState);

        while (randomGame.getGipfBoardState().players.winner() == null) {
            try {
                randomGame.applyCurrentPlayerMove();
            } catch (GameEndException e) {
                break;
            }
        }

        return randomGame.getGipfBoardState().players.winner().pieceColor;
    }
}