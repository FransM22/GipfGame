package GUI.GipfBoardComponent;

import GUI.GipfBoardComponent.DrawableObjects.*;
import GameLogic.Game;
import GameLogic.Move;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The GipfBoardComponent is a Swing component which can be embedded in a JPanel. This component only shows the board itself
 * and everything that is positioned on it.
 * <p/>
 * Created by frans on 18-9-2015.
 */
public class GipfBoardComponent extends JComponent {
    public final Game game;


    // The next fields have a default scope, as they need to be accessed from GipfBoardComponentMoueListener
    Set<Position> selectablePositions = new HashSet<>(Arrays.asList(UIval.get().filledCirclePositions));                    // Default; needs to be accessible from GipfBoardComponentMouseListener
    public Position selectedPosition;                                                                                  // The position that is currently selected for a new move
    Set<Position> moveToPositions = new HashSet<>(Arrays.asList(new Position('h', 2), new Position('h', 3)));
    public Position selectedMoveToPosition;
    public Position currentHoverPosition = null;

    /**
     * Creates a component in which a Gipf board can be shown. Only works for standard sized boards
     *
     * @param game the game of which the state is shown in the GipfBoardComponent
     */
    public GipfBoardComponent(Game game) {
        this.game = game;

        setPreferredSize(new Dimension(600, 600));
    }

    public static void main(String argv[]) {
        Game game = new Game();
        GipfBoardComponent gipfBoardComponent = new GipfBoardComponent(game);
        gipfBoardComponent.addMouseListener(new GipfBoardComponentMouseListener(gipfBoardComponent));

        // These are only for checking whether the component works
        game.setPiece(new Position('b', 2), Game.Piece.WHITE_SINGLE);
        game.setPiece(new Position('b', 3), Game.Piece.WHITE_SINGLE);
        game.setPiece(new Position('b', 4), Game.Piece.WHITE_SINGLE);
        game.setPiece(new Position('b', 5), Game.Piece.WHITE_SINGLE);

        game.setPiece(new Position('c', 6), Game.Piece.BLACK_SINGLE);
        game.setPiece(new Position('d', 7), Game.Piece.BLACK_SINGLE);
        game.setPiece(new Position('e', 8), Game.Piece.BLACK_SINGLE);
        game.setPiece(new Position('f', 7), Game.Piece.BLACK_SINGLE);
        game.setPiece(new Position('g', 6), Game.Piece.BLACK_SINGLE);
        game.setPiece(new Position('h', 5), Game.Piece.BLACK_SINGLE);

        game.setPiece(new Position('e', 4), Game.Piece.BLACK_GIPF);
        game.setPiece(new Position('f', 6), Game.Piece.WHITE_GIPF);

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(gipfBoardComponent);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Main paint class, from here all the other methods that paint something are called
     *
     * @param g the Graphics object to which is drawn
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        DrawableObject gipfPieces = new GipfPieces(g2, this);
        DrawableObject selectedPosition = new SelectedPosition(g2, this);
        DrawableObject hoverCircle = new HoverCircle(g2, this);
        DrawableObject drawableGipfBoard = new DrawableGipfBoard(g2, this);
        DrawableObject filledCircles = new FilledCircles(g2, this);
        DrawableObject selectedMoveToArrow = new SelectedMoveToArrow(g2, this);
        DrawableObject positionNames = new PositionNames(g2, this);

        if (UIval.get().antiAliasingEnabled) {
            // Set anti aliasing. If enabled, it makes the drawing much slower, but look nicer
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // The order of the following methods determines the order in which the elements are drawn. A method on top indicates
        // that the object is drawn at the bottom.
        drawableGipfBoard.draw();
        filledCircles.draw();
        selectedMoveToArrow.draw();
        hoverCircle.draw();
        gipfPieces.draw();
        selectedPosition.draw();
        positionNames.draw();
    }
}
