package GUI.GipfBoardComponent;

import GameLogic.Game;
import GameLogic.Move;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The GipfBoardComponent is a Swing component which can be embedded in a JPanel. This component only shows the board itself
 * and everything that is positioned on it.
 * <p/>
 * Created by frans on 18-9-2015.
 */
public class GipfBoardComponent extends JComponent {
    // Colors used
    final static Color backgroundColor = new Color(0xD2FF9B);          // The background of the component
    static final Color lineColor = new Color(0x8D8473);                // The lines showing how pieces are allowed to move
    private static final Color positionNameColor = lineColor;                  // Color of position names
    final Game game;
    // Variables which can be changed to change the look
    final int nrOfColumnsOnGipfBoard = 9;                   // The number of columns on a gipf board. Only edit if the GipfBoard class can handle it
    final int nrOfRowsOnGipfBoard = 9;                      // The number of rows on a gipf board. Only edit if the GipfBoard class can handle it
    final int marginSize = 25;                              // The margin on the sides of the board
    // Some basic flags to set
    private final boolean displayPiecePosition = false;             // Displays the piece positions above the pieces. This only works for displaying the position of pieces, not of given positions
    private final boolean drawFilledCircles = true;                 // Draw filled circles on the given positions (at the ends of the lines on the board)
    private final boolean antiAliasingEnabled = true;               // Enable anti aliasing. If disabled, the drawing will be much faster. Can be disabled for performance
    private final boolean hoveringEnabled = true;                   // Displays a circle on the position where the mouse is hovering
    private final int filledCircleSize = 15;                        // The size of the filled circles
    // Line types
    private final Stroke moveToArrowStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6f, 6f}, 0.0f);
    private final Font positionNameFont = new Font("default", Font.BOLD, 14);
    private final Color centerColor = new Color(0xE5FFCE);              // The hexagon in the center
    private final Color filledCircleColor = backgroundColor;            // Color of the circles that are filled (on the edges of the board)
    private final Color filledCircleBorderColor = new Color(0x7D8972);  // Border color of the filled circles
    private final Color moveToArrowColor = lineColor;                   // The line indicating where the player can move his piece
    // These mark the center hexagon on the board
    private final Position[] centerCornerPositions = {            // Contains the corners of the center hexagon. Distinguishes the part where pieces can end up from the background
            new Position('b', 2),
            new Position('b', 5),
            new Position('e', 8),
            new Position('h', 5),
            new Position('h', 2),
            new Position('e', 2)
    };
    private final Position[] topAndBottomPositions = {
            new Position('a', 1),
            new Position('b', 1),
            new Position('c', 1),
            new Position('d', 1),
            new Position('e', 1),
            new Position('f', 1),
            new Position('g', 1),
            new Position('h', 1),
            new Position('i', 1),
            new Position('a', 5),
            new Position('b', 6),
            new Position('c', 7),
            new Position('d', 8),
            new Position('e', 9),
            new Position('f', 8),
            new Position('g', 7),
            new Position('h', 6),
            new Position('i', 5)
    };
    private final Position[] sidePositions = {
            new Position('a', 2),
            new Position('a', 3),
            new Position('a', 4),
            new Position('i', 2),
            new Position('i', 3),
            new Position('i', 4)
    };
    // These positions are named on the board
    private final Position[] namedPositionsOnBoard = topAndBottomPositions;
    // These positions have a circle on their position
    // Code concatenates two arrays via streams, see http://stackoverflow.com/a/23188881
    private final Position[] filledCirclePositions = Stream.concat(Arrays.stream(topAndBottomPositions), Arrays.stream(sidePositions)).toArray(Position[]::new);
    /*
     * line sets are used for easier drawing of the lines which indicate how the player is allowed to move.
     * Each line set contains a start and endpoint of a line on the board. In addition to each of these two points a direction
     * is stored, in which the next (parallel) line can be found. The last number indicates how many times a parallel
     * line can be drawn.
     * Each set of parallel lines is divided into two, because the direction in which the points move changes halfway
     */
    private final LineSet[] lineSets = {
            new LineSet(new Position('a', 2), new Position('f', 1), Move.Direction.NORTH, Move.Direction.NORTH_EAST, 4),
            new LineSet(new Position('b', 6), new Position('i', 2), Move.Direction.NORTH_EAST, Move.Direction.NORTH, 3),
            new LineSet(new Position('d', 1), new Position('i', 2), Move.Direction.NORTH_WEST, Move.Direction.NORTH, 4),
            new LineSet(new Position('a', 2), new Position('h', 6), Move.Direction.NORTH, Move.Direction.NORTH_WEST, 3),
            new LineSet(new Position('b', 1), new Position('b', 6), Move.Direction.SOUTH_EAST, Move.Direction.NORTH_EAST, 4),
            new LineSet(new Position('f', 1), new Position('f', 8), Move.Direction.NORTH_EAST, Move.Direction.SOUTH_EAST, 3)
    };

    // The next fields have a default scope, as they need to be accessed from GipfBoardComponentMoueListener
    Set<Position> selectablePositions = new HashSet<>(Arrays.asList(filledCirclePositions));                    // Default; needs to be accessible from GipfBoardComponentMouseListener
    Position selectedPosition;                                                                                  // The position that is currently selected for a new move
    Set<Position> moveToPositions = new HashSet<>(Arrays.asList(new Position('h', 2), new Position('h', 3)));
    Position selectedMoveToPosition;
    Position currentHoverPosition = null;

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
        PositionHelper positionHelper = new PositionHelper(this);
        GipfPiecePainter gipfPiecePainter = new GipfPiecePainter(this);

        if (antiAliasingEnabled) {
            // Set anti aliasing. If enabled, it makes the drawing much slower, but look nicer
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // The order of the following methods determines the order in which the elements are drawn. A method on top indicates
        // that the object is drawn at the bottom.
        paintBoard(g2, positionHelper);
        if (drawFilledCircles) {
            paintFilledCircles(g2, positionHelper);
        }
        paintSelectedMoveToArrow(g2, positionHelper);
        gipfPiecePainter.paintHoverCircle(g2, positionHelper);
        paintPieces(g2, gipfPiecePainter, positionHelper);
        gipfPiecePainter.paintSelectedPosition(g2, positionHelper);
        drawPositionNames(g2, positionHelper);
    }

    /**
     * Paints the board itself, including the background, center part, and the lines. This method is usually called from
     * the paintComponent method.
     *
     * @param g2             the Graphics2D object to which is drawn
     * @param positionHelper
     */
    private void paintBoard(Graphics2D g2, PositionHelper positionHelper) {
        // Paint the background of the component
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(centerColor);
        // Java8 stuff. Basically maps each of the positions in cornerPositions to a x and y value.
        g2.fillPolygon(
                Arrays.stream(centerCornerPositions).mapToInt(positionHelper::positionToScreenX).toArray(),
                Arrays.stream(centerCornerPositions).mapToInt(positionHelper::positionToScreenY).toArray(),
                centerCornerPositions.length
        );

        // Draw the lines
        g2.setColor(lineColor);

        for (LineSet lineSet : lineSets) {
            int startDeltaPos = game.getDeltaPos(lineSet.nextStart);
            int endDeltaPos = game.getDeltaPos(lineSet.nextEnd);

            for (int lineNr = 0; lineNr < lineSet.nr; lineNr++) {
                Position start = new Position(lineSet.start.getPosId() + lineNr * startDeltaPos);
                Position end = new Position(lineSet.end.getPosId() + lineNr * endDeltaPos);

                g2.drawLine(
                        positionHelper.positionToScreenX(start),
                        positionHelper.positionToScreenY(start),
                        positionHelper.positionToScreenX(end),
                        positionHelper.positionToScreenY(end)
                );
            }
        }
    }

    private void paintSelectedMoveToArrow(Graphics2D g2, PositionHelper positionHelper) {
        if (selectedMoveToPosition != null) {
            // Get the allowed positions from here
            g2.setColor(moveToArrowColor);
            g2.setStroke(moveToArrowStroke);
            g2.drawLine(
                    positionHelper.positionToScreenX(selectedPosition),
                    positionHelper.positionToScreenY(selectedPosition),
                    positionHelper.positionToScreenX(selectedMoveToPosition),
                    positionHelper.positionToScreenY(selectedMoveToPosition)
            );
        }
    }

    private void paintFilledCircles(Graphics2D g2, PositionHelper positionHelper) {
        for (Position position : filledCirclePositions) {
            PrimitiveShapeHelper.centerCircleOn(g2, positionHelper.positionToScreenX(position), positionHelper.positionToScreenY(position), filledCircleSize, filledCircleColor, filledCircleBorderColor);
        }
    }

    private void drawPositionNames(Graphics2D g2, PositionHelper positionHelper) {
        g2.setColor(positionNameColor);

        for (Position position : namedPositionsOnBoard) {
            g2.setColor(positionNameColor);
            g2.setFont(positionNameFont);
            g2.drawString(position.getName(), positionHelper.positionToScreenX(position) + 10, positionHelper.positionToScreenY(position) + 5);   // Translated by (10, 5), to make text not overlap with lines
        }
    }

    private void paintPieces(Graphics2D g2, GipfPiecePainter gipfPiecePainter, PositionHelper positionHelper) {
        for (Map.Entry<Position, Game.Piece> entry : game.getGipfBoard().getPieceMap().entrySet()) {
            Position position = entry.getKey();
            Game.Piece piece = entry.getValue();

            gipfPiecePainter.drawPiece(g2, position, piece);
        }

        gipfPiecePainter.paintSelectedPosition(g2, positionHelper);
        gipfPiecePainter.paintHoverCircle(g2, positionHelper);
    }

    private class LineSet {
        final Position start;
        final Position end;
        final Move.Direction nextStart;
        final Move.Direction nextEnd;
        final int nr;

        public LineSet(Position start, Position end, Move.Direction nextStart, Move.Direction nextEnd, int nr) {
            this.start = start;
            this.end = end;
            this.nextStart = nextStart;
            this.nextEnd = nextEnd;
            this.nr = nr;
        }
    }
}
