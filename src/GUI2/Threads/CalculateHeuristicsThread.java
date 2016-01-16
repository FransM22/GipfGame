package GUI2.Threads;

import GameLogic.GipfBoardState;

/**
 * Created by frans on 8-1-2016.
 */
public class CalculateHeuristicsThread extends Thread {
    private static final CalculateHeuristicsThread INSTANCE = new CalculateHeuristicsThread();
    private static GipfBoardState rootGipfBoardState = null;

    private CalculateHeuristicsThread() {
        super(() -> {
            while (true) {
                try {
                    if (rootGipfBoardState != null) {
                        // Traverse with iterative deepening
                        if (rootGipfBoardState.getUnexploredChildren().size() > 0)
                            rootGipfBoardState.exploreAllChildren();

                        for (GipfBoardState childState : rootGipfBoardState.exploredChildren.values()) {
                            childState.boardStateProperties.blobValueMax = childState.boardStateProperties.blobValue;
                            childState.boardStateProperties.blobValueMin = childState.boardStateProperties.blobValue;

                            Thread.sleep(10);
                            recursiveDepthLimited(childState, childState, 2);
                        }
                    } else {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    // Nothing
                }
            }
        });

        setName("Thread-CalculateHeuristicsThread");

        start();
    }

    private static void recursiveDepthLimited(GipfBoardState root, GipfBoardState nodeToExplore, int maxDepthFromRoot) {

        // Backpropagate to the root node
        nodeToExplore.boardStateProperties.updateBoardState();
        if (nodeToExplore.boardStateProperties.blobValue < root.boardStateProperties.blobValueMin) {
            root.boardStateProperties.blobValueMin = nodeToExplore.boardStateProperties.blobValue;
        }
        if (nodeToExplore.boardStateProperties.blobValue > root.boardStateProperties.blobValueMax) {
            root.boardStateProperties.blobValueMax = nodeToExplore.boardStateProperties.blobValue;
        }

        if (nodeToExplore.boardStateProperties.depth - root.boardStateProperties.depth < maxDepthFromRoot) {
            if (nodeToExplore.getUnexploredChildren().size() > 0) nodeToExplore.exploreAllChildren();

            for (GipfBoardState childNodeToExplore : nodeToExplore.exploredChildren.values()) {
                recursiveDepthLimited(root, childNodeToExplore, maxDepthFromRoot);
            }
        }
    }

    public static void setCurrentRoot(GipfBoardState rootGipfBoardState) {
        CalculateHeuristicsThread.rootGipfBoardState = rootGipfBoardState;
    }
}
