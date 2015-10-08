package GUI.GipfBoardComponent;

import GUI.GipfBoardComponent.DrawableObjects.*;
import GUI.UIval;
import GameLogic.Game.*;
import GameLogic.Piece;
import GameLogic.PieceColor;
import GameLogic.Player;
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
    // The next fields have a default scope, as they need to be accessed from GipfBoardComponentMouseListener
    final Set<Position> selectableStartPositions = new HashSet<>(Arrays.asList(UIval.get().filledCirclePositions));
    public Game game;
    public Position selectedStartPosition;                                                                              // The position that is currently selected as start of a new move
    public Position selectedMoveToPosition;                                                                             // Position that is selected as the end point of a move
    public Set<Position> selectableRemovePositions;
    public Set<Position> selectedRemovePositions = new HashSet<>();
    public Position currentHoverPosition = null;                                                                        // The position where the user of the UI is currently hovering over

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
        Game game = null;   // TODO Fix this construction
        GipfBoardComponent gipfBoardComponent = new GipfBoardComponent(game);
        game = new BasicGame();

        // These are only for checking whether the component works
        game.setPiece(game.getGipfBoardState(), new Position('b', 2), Piece.WHITE_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('b', 3), Piece.WHITE_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('b', 4), Piece.WHITE_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('b', 5), Piece.WHITE_SINGLE);

        game.setPiece(game.getGipfBoardState(), new Position('c', 6), Piece.BLACK_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('d', 7), Piece.BLACK_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('e', 8), Piece.BLACK_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('f', 7), Piece.BLACK_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('g', 6), Piece.BLACK_SINGLE);
        game.setPiece(game.getGipfBoardState(), new Position('h', 5), Piece.BLACK_SINGLE);

        game.setPiece(game.getGipfBoardState(), new Position('e', 4), Piece.BLACK_GIPF);
        game.setPiece(game.getGipfBoardState(), new Position('f', 6), Piece.WHITE_GIPF);

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
                new SelectedStartPosition(g2, this, Collections.singleton(selectedStartPosition)),
                new SelectedRemovePositions(g2, this),
                new PositionNames(g2, this),
                new GameOverMessage(g2, this)
        );

        drawableObjects.stream().forEach(DrawableObject::draw);
    }

    private void enableAntiAliasing(Graphics2D g2) {
        // Set anti aliasing. If enabled, it makes the drawing much slower, but look nicer
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Color getColorOfPlayer(Player player) {
        if (player.pieceColor == PieceColor.BLACK)
            return UIval.get().blackPieceColor;
        if (player.pieceColor == PieceColor.WHITE)
            return UIval.get().whitePieceColor;

        return Color.red;
    }

    public Color getBorderColorOfPlayer(Player player) {
        if (player.getIsPlacingGipfPieces()) {
            return UIval.get().gipfPieceBorderColor;
        } else {
            return UIval.get().singlePieceBorderColor;
        }
    }

    public void clearSelectedPositions() {
        selectedStartPosition = null;
        selectedMoveToPosition = null;
        repaint();
    }

    public void newGame(GameType gameType) {
        switch (gameType) {
            case basic:
                game = new BasicGame();
                break;
            case standard:
                game = new StandardGame();
                break;
            case tournament:
                game = new TournamentGame();
        }
        clearSelectedPositions();
    }

    public static int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
}
