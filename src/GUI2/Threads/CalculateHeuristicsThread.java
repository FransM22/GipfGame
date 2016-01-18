package GUI2.Threads;

import GameLogic.GipfBoardState;

/**
 * Created by frans on 8-1-2016.
 */
public class CalculateHeuristicsThread extends Thread {
    private static final CalculateHeuristicsThread INSTANCE = new CalculateHeuristicsThread();
    private static GipfBoardState rootGipfBoardState = null;
    static boolean calculationIsDone = true;

    private CalculateHeuristicsThread() {
        super(() -> {
            while (true) {
                try {
                    if (!calculationIsDone) {
                        // Traverse with iterative deepening
                        if (rootGipfBoardState.getUnexploredChildren().size() > 0) {
                            rootGipfBoardState.exploreAllChildren();
                        }
                        recursiveDepthLimited(rootGipfBoardState, rootGipfBoardState, 3);
                        calculationIsDone = true;
                    } else {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                }
            }
        });

        setName("CalculateHeuristicsThread");

        start();
    }

    private static void recursiveDepthLimited(GipfBoardState root, GipfBoardState nodeToExplore, int maxDepthFromRoot) throws InterruptedException {
        nodeToExplore.boardStateProperties.updateBoardState();

        // Backpropagate to the direct child of the root state node
        GipfBoardState directChildOfRoot = directChildBelow(root, nodeToExplore);
        if (nodeToExplore.boardStateProperties.weightedHeuristic < root.boardStateProperties.heuristicMin) {
            directChildOfRoot.boardStateProperties.heuristicMin = nodeToExplore.boardStateProperties.weightedHeuristic;
        }
        if (nodeToExplore.boardStateProperties.weightedHeuristic > root.boardStateProperties.heuristicMax) {
            directChildOfRoot.boardStateProperties.heuristicMax = nodeToExplore.boardStateProperties.weightedHeuristic;
        }

        if (nodeToExplore.boardStateProperties.depth - root.boardStateProperties.depth < maxDepthFromRoot) {
            if (nodeToExplore.getUnexploredChildren().size() > 0) nodeToExplore.exploreAllChildren();

            for (GipfBoardState childNodeToExplore : nodeToExplore.exploredChildren.values()) {
                if (calculationIsDone) { throw new InterruptedException(); }
                recursiveDepthLimited(root, childNodeToExplore, maxDepthFromRoot);
            }
        }
    }

    public static void setCurrentRootState(GipfBoardState newRootState) {
        rootGipfBoardState = newRootState;
        calculationIsDone = false;
    }

    private static GipfBoardState directChildBelow(GipfBoardState root, GipfBoardState grandChild) {
        if (grandChild.equals(root)) return  grandChild;

        GipfBoardState currentGipfBoardState = grandChild;
        while (!currentGipfBoardState.parent.equals(root)) {
            currentGipfBoardState = currentGipfBoardState.parent;
        }

        return currentGipfBoardState;
    }
}
