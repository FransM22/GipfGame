package GUI;

import GameLogic.GipfBoard;
import GameLogic.Move;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * The GipfBoardComponent is a Swing component which can be embedded in a JPanel. This component only shows the board itself
 * and everything that is positioned on it.
 *
 * Created by frans on 18-9-2015.
 */
class GipfBoardComponent extends JComponent implements MouseListener{
    // Some basic flags to set
    private final boolean displayPiecePosition = false;             // Displays the piece positions above the pieces. This only works for displaying the position of pieces, not of given positions
    private final boolean drawFilledCircles = true;                 // Draw filled circles on the given positions (at the ends of the lines on the board)
    private final boolean antiAliasingEnabled = true;               // Enable anti aliasing. If disabled, the drawing will be much faster. Can be disabled for performance
    private final boolean hoveringEnabled = true;                   // Displays a circle on the position where the mouse is hovering


    // Variables which can be changed to change the look
    private final int pieceSize = 50;                               // The size in pixels in which the pieces are displayed
    private final int nrOfColumnsOnGipfBoard = 9;                   // The number of columns on a gipf board. Only edit if the GipfBoard class can handle it
    private final int nrOfRowsOnGipfBoard = 9;                      // The number of rows on a gipf board. Only edit if the GipfBoard class can handle it
    private final int marginSize = 25;                              // The margin on the sides of the board
    private final int filledCircleSize = 15;                        // The size of the filled circles
    private final int hoverCircleSize = 20;

    private final int hoverUpdateIntervalMs = 100;                  // The update interval in milliseconds of the component when the hover circle is displayed

    // Line types
    private final Stroke normalPieceStroke = new BasicStroke(1);
    private final Stroke gipfPieceStroke = new BasicStroke(4.0f);
    private final Stroke hoverPositionStroke = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {6f, 6f}, 0.0f);     // A dashed stroke style. Don't really know how this works.

    // Colors used
    private final Color backgroundColor = new Color(0xD2FF9B);          // The background of the component
    private final Color centerColor = new Color(0xE5FFCE);              // The hexagon in the center
    private final Color lineColor = new Color(0x8D8473);                // The lines showing how pieces are allowed to move
    private final Color whiteSingleColor = new Color(0xF9F9F9);         // Color of the normal white piece
    private final Color whiteGipfColor = whiteSingleColor;              // Color of the white gipf piece
    private final Color blackSingleColor = new Color(0x525252);         // Color of the normal black piece
    private final Color blackGipfColor = blackSingleColor;              // Color of the black gipf piece
    private final Color singlePieceBorderColor = Color.black;           // Border color of normal single pieces
    private final Color gipfPieceBorderColor = new Color(0xDA0000);     // Border color of gipf pieces
    private final Color positionNameColor = Color.red;                  // Color of position names
    private final Color filledCircleColor = new Color(0xD2FF9B);        // Color of the circles that are filled
    private final Color filledCircleBorderColor = new Color(0x7D8972);  // Border color of the filled circles
    private final Color hoverBorderColor = new Color(0x8D8473);
    private final Color hoverFillColor = new Color(0xFFFFFB);

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
    private final GipfBoard gipfBoard;
    private Position currentHoverPosition = null;
    private Thread hoverThread;


    /**
     * Creates a component in which a Gipf board can be shown. Only works for standard sized boards
     *
     * @param gipfBoard the GipfBoard that is shown in the GipfBoardComponent
     */
    public GipfBoardComponent(GipfBoard gipfBoard) {
        this.gipfBoard = gipfBoard;

        setPreferredSize(new Dimension(600, 600));
    }

    public static void main(String argv[]) {
        GipfBoard gb = new GipfBoard();
        GipfBoardComponent gipfBoardComponent = new GipfBoardComponent(gb);
        gipfBoardComponent.addMouseListener(gipfBoardComponent);

        // These are only for checking whether the component works
        gb.setPiece(new Position('b', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('b', 3), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('b', 4), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('b', 5), GipfBoard.Piece.WHITE_SINGLE);

        gb.setPiece(new Position('c', 6), GipfBoard.Piece.BLACK_SINGLE);
        gb.setPiece(new Position('d', 7), GipfBoard.Piece.BLACK_SINGLE);
        gb.setPiece(new Position('e', 8), GipfBoard.Piece.BLACK_SINGLE);
        gb.setPiece(new Position('f', 7), GipfBoard.Piece.BLACK_SINGLE);
        gb.setPiece(new Position('g', 6), GipfBoard.Piece.BLACK_SINGLE);
        gb.setPiece(new Position('h', 5), GipfBoard.Piece.BLACK_SINGLE);

        gb.setPiece(new Position('e', 4), GipfBoard.Piece.BLACK_GIPF);
        gb.setPiece(new Position('f', 6), GipfBoard.Piece.WHITE_GIPF);

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

        if (antiAliasingEnabled) {
            // Set anti aliasing. If enabled, it makes the drawing much slower, but look nicer
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // The order of the following methods determines the order in which the elements are drawn. A method on top indicates
        // that the object is drawn at the bottom.
        paintBoard(g2);
        if (drawFilledCircles) {
            paintFilledCircles(g2);
        }
        paintHoverCircle(g2, currentHoverPosition);
        paintPieces(g2);
        drawPositionNames(g2);
    }

    /**
     * Paints the board itself, including the background, center part, and the lines. This method is usually called from
     * the paintComponent method.
     *
     * @param g2 the Graphics2D object to which is drawn
     */
    private void paintBoard(Graphics2D g2) {
        // Paint the background of the component
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(centerColor);
        // Java8 stuff. Basically maps each of the positions in cornerPositions to a x and y value.
        g2.fillPolygon(
                Arrays.stream(centerCornerPositions).mapToInt(this::positionToScreenX).toArray(),
                Arrays.stream(centerCornerPositions).mapToInt(this::positionToScreenY).toArray(),
                centerCornerPositions.length
        );

        // Draw the lines
        g2.setColor(lineColor);

        for (LineSet lineSet : lineSets) {
            int startDeltaPos = gipfBoard.getDeltaPos(lineSet.nextStart);
            int endDeltaPos = gipfBoard.getDeltaPos(lineSet.nextEnd);

            for (int lineNr = 0; lineNr < lineSet.nr; lineNr++) {
                Position start = new Position(lineSet.start.getPosId() + lineNr * startDeltaPos);
                Position end = new Position(lineSet.end.getPosId() + lineNr * endDeltaPos);

                g2.drawLine(
                        positionToScreenX(start),
                        positionToScreenY(start),
                        positionToScreenX(end),
                        positionToScreenY(end)
                );
            }
        }
    }

    private void paintHoverCircle(Graphics2D g2, Position position) {
        if (position != null) {
            centerCircleOn(g2, positionToScreenX(position), positionToScreenY(position), hoverCircleSize, hoverFillColor, hoverBorderColor, hoverPositionStroke);
        }
    }

    private void centerCircleOn(Graphics2D g2, int x, int y, int size, Color fillColor, Color borderColor, Stroke strokeStyle) {
        g2.setColor(fillColor);
        g2.fillOval(
                x - (size / 2),
                y - (size / 2),
                size,
                size
        );

        g2.setStroke(strokeStyle);
        g2.setColor(borderColor);
        g2.drawOval(
                x - (size / 2),
                y - (size / 2),
                size,
                size
        );
    }

    private void centerCircleOn(Graphics2D g2, int x, int y, int size, Color fillColor, Color borderColor) {
        centerCircleOn(g2, x, y, size, fillColor, borderColor, new BasicStroke(1));
    }

    private void paintFilledCircles(Graphics2D g2) {
        for (Position position : filledCirclePositions) {
            centerCircleOn(g2, positionToScreenX(position), positionToScreenY(position), filledCircleSize, filledCircleColor, filledCircleBorderColor);
        }
    }

    private void drawPiece(Graphics2D g2, Position position, GipfBoard.Piece piece) {
        Color fillColor = null;     // Needs an initial value
        Color borderColor = null;   // Needs an initial value
        Stroke strokeType = normalPieceStroke;

        switch (piece) {
            case WHITE_SINGLE:
                fillColor = whiteSingleColor;
                borderColor = singlePieceBorderColor;
                break;
            case WHITE_GIPF:
                fillColor = whiteGipfColor;
                borderColor = gipfPieceBorderColor;
                strokeType = gipfPieceStroke;
                break;
            case BLACK_SINGLE:
                fillColor = blackSingleColor;
                borderColor = singlePieceBorderColor;
                break;
            case BLACK_GIPF:
                fillColor = blackGipfColor;
                borderColor = gipfPieceBorderColor;
                strokeType = gipfPieceStroke;
                break;
        }
        centerCircleOn(g2, positionToScreenX(position), positionToScreenY(position), pieceSize, fillColor, borderColor, strokeType);

        if (displayPiecePosition) {
            g2.setColor(positionNameColor);
            g2.drawString(position.getName(), positionToScreenX(position), positionToScreenY(position));
        }
    }

    private void drawPositionNames(Graphics2D g2) {
        g2.setColor(positionNameColor);

        for (Position position : namedPositionsOnBoard) {
            g2.setColor(positionNameColor);
            g2.drawString(position.getName(), positionToScreenX(position) + 10, positionToScreenY(position));   // x + 10, to make text not overlap with lines
        }
    }

    private void paintPieces(Graphics2D g2) {
        for (Map.Entry<Position, GipfBoard.Piece> entry : gipfBoard.getPieceMap().entrySet()) {
            Position position = entry.getKey();
            GipfBoard.Piece piece = entry.getValue();

            drawPiece(g2, position, piece);
        }
    }

    private int positionToScreenY(Position p) {
        int height = getHeight() - (2 * marginSize);
        int colNumber = p.getColName() - 'a' + 1;               // Column number, starting at 1
        double rowHeight = height / (nrOfRowsOnGipfBoard - 1);  // The first and last piece are shown at the beginning and end, so we only need nrOfRows - 1 equally divided rows

        if (colNumber <= 5) {
            return (int) Math.round(height - (p.getRowNumber() - 1 - 0.5 * (colNumber - 5)) * rowHeight) + marginSize;
        } else {
            return (int) Math.round(height - (p.getRowNumber() - 1 + 0.5 * (colNumber - 5)) * rowHeight) + marginSize;
        }
    }

    private int positionToScreenX(Position p) {
        int width = getWidth() - (2 * marginSize);
        // nrOfColumns - 1, because n columns are  divided by n - 1 equal spaces
        return (p.getColName() - 'a') * (width / (nrOfColumnsOnGipfBoard - 1)) + marginSize;
    }

    private Position screenCoordinateToPosition(int screenX, int screenY) {
        // Calculate the column and row sizes
        int columnWidth = (getWidth() - (2 * marginSize)) / (nrOfColumnsOnGipfBoard - 1);
        int rowHeight = (getHeight() - (2 * marginSize)) / (nrOfRowsOnGipfBoard - 1);

        // Take into account the margins. Also flip the y coordinate so we can access the board coordinates start from
        // row 1.
        int xOnBoard = screenX - marginSize;
        int yOnBoard = getHeight() - screenY - marginSize;

        int columnNumber = (int) ((Math.round((double) xOnBoard / columnWidth)));   // The column number, starting at 0 (!)
        char columnName = (char) (columnNumber + 'a');

        // These numbers do not take into account that the rows are not horizontally. This means that the result is only
        // correct for the middle column
        double rowNumberStartFromBottom = (double) yOnBoard / rowHeight;
        int horizontalDistanceFromCenter = Math.abs(screenX - (getWidth()/2));

        double columnsFromCenter = (double) horizontalDistanceFromCenter / columnWidth;

        // This row number is the correct row number according to the letter notation (a3). This means that the first row
        // is number 1.
        double rowNumberFixed = (rowNumberStartFromBottom - (0.5 * columnsFromCenter)) + 1;
        int rowNumberInt = (int) Math.round(rowNumberFixed);

        return new Position(columnName, rowNumberInt);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gipfBoard.setPiece(
                screenCoordinateToPosition(e.getX(), e.getY()),
                GipfBoard.Piece.BLACK_SINGLE);

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

        if (hoveringEnabled) {
            hoverThread = new Thread(new UpdateHoverPosition(this));
            hoverThread.start();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (hoveringEnabled) {
            hoverThread.interrupt();

            currentHoverPosition = null;
            repaint();
        }
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

    private class UpdateHoverPosition implements Runnable {
        GipfBoardComponent gipfBoardComponent;
        private Position previousPosition = null;

        UpdateHoverPosition(GipfBoardComponent gipfBoardComponent) {
            this.gipfBoardComponent = gipfBoardComponent;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(hoverUpdateIntervalMs);
                } catch (InterruptedException e) {
                    // Interrupt the thread
                    break;
                }

                Point mouseLocation = MouseInfo.getPointerInfo().getLocation();                             // Get the mouse position relative to the screen
                Point componentPosition = getLocationOnScreen();                                            // Get the component position relative to the screen
                mouseLocation.translate((int) -componentPosition.getX(), (int) - componentPosition.getY()); // Calculate the mouse position relative to the component

                // Only update the position if the new position is different from the old position, and if the new
                // position is actually located on the board
                Position newHoverPosition = screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
                if (newHoverPosition != previousPosition) {
                    if (gipfBoardComponent.gipfBoard.isPositionOnBoard(newHoverPosition)) {
                        currentHoverPosition = screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());
                        previousPosition = currentHoverPosition;
                    }
                    else {
                        currentHoverPosition = null;
                    }
                    gipfBoardComponent.repaint();
                }
            }
        }
    }
}
