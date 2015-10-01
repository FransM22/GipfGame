package GUI.GipfBoardComponent;

import GUI.GipfBoardComponent.DrawableObjects.*;
import GUI.UIval;
import GameLogic.Game;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The GipfBoardComponent is a Swing component which can be embedded in a JPanel. This component only shows the board itself
 * and everything that is positioned on it.
 * <p/>
 * Created by frans on 18-9-2015.
 */
public class GipfBoardComponent extends JComponent {
    public final Game game;
    public Position selectedPosition;                                                                                   // The position that is currently selected as start of a new move
    public Position selectedMoveToPosition;                                                                             // Position that is selected as the end point of a move
    public Position currentHoverPosition = null;                                                                        // The position where the user of the UI is currently hovering over

    // The next fields have a default scope, as they need to be accessed from GipfBoardComponentMouseListener
    Set<Position> selectablePositions = new HashSet<>(Arrays.asList(UIval.get().filledCirclePositions));

    /**
     * Creates a component in which a Gipf board can be shown. Only works for standard sized boards
     *
     * @param game the game of which the state is shown in the GipfBoardComponent
     */
    public GipfBoardComponent(Game game) {
        this.game = game;
        addMouseListener(new GipfBoardComponentMouseListener(this));

        setPreferredSize(new Dimension(600, 600));
    }

    public static void main(String argv[]) {
        Game game = new Game();
        GipfBoardComponent gipfBoardComponent = new GipfBoardComponent(game);

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

        if (UIval.get().antiAliasingEnabled)
            enableAntiAliasing(g2);

        // The object (sets) that are drawn on the component. The order *does* matter.
        List<DrawableObject> drawableObjects = Arrays.asList(
                new DrawableGipfBoard(g2, this),
                new FilledCircles(g2, this, new HashSet<>(Arrays.asList(UIval.get().filledCirclePositions))),
                new GipfPieces(g2, this),
                new SelectedMoveToArrow(g2, this),
                new HoverCircle(g2, this, Collections.singleton(currentHoverPosition)),
                new SelectedPosition(g2, this, Collections.singleton(selectedPosition)),
                new PositionNames(g2, this)
        );

        drawableObjects.stream().forEach(DrawableObject::draw);
    }

    private void enableAntiAliasing(Graphics2D g2) {
        // Set anti aliasing. If enabled, it makes the drawing much slower, but look nicer
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Color getColorOfPlayer(Game.Player player) {
        if (player.pieceColor == Game.PieceColor.BLACK)
            return UIval.get().blackSingleColor;
        if (player.pieceColor == Game.PieceColor.WHITE)
            return UIval.get().whiteSingleColor;

        return Color.red;
    }
}
