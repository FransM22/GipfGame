package GUI;

import GameLogic.GipfBoard;
import GameLogic.Move;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by frans on 18-9-2015.
 */
public class GipfBoardComponent extends JComponent {
    // Variables which can be changed to change the look
    final int pieceSize = 50;                               // The size in pixels in which the pieces are displayed
    final boolean displayPiecePosition = false;             // Displays the piece positions above the pieces. This only works for displaying the position of pieces, not of given positions
    final boolean drawFilledCircles = true;                 // Draw filled circles on the given positions
    final int nrOfColumnsOnGipfBoard = 9;                   // The number of columns on a gipf board. Only edit if the GipfBoard class can handle it
    final int nrOfRowsOnGipfBoard = 9;                      // The number of rows on a gipf board. Only edit if the GipfBoard class can handle it
    final int marginSize = 10;                              // The margin on the sides of the board
    final int filledCircleSize = 10;                        // The size of the filled circles


    // Line types
    final Stroke normalPieceStroke = new BasicStroke(1);
    final Stroke gipfPieceStroke = new BasicStroke(3);

    // Colors used
    final Color backgroundColor = new Color(0xD2FF9B);          // The background of the component
    final Color centerColor = new Color(0xE5FFCE);              // The hexagon in the center
    final Color lineColor = new Color(0x8D8473);                // The lines showing how pieces are allowed to move
    final Color whiteSingleColor = new Color(0x525252);         // Color of the normal white piece
    final Color whiteGipfColor = whiteSingleColor;              // Color of the white gipf piece
    final Color blackSingleColor = new Color(0xF9F9F9);         // Color of the normal black piece
    final Color blackGipfColor = blackSingleColor;              // Color of the black gipf piece
    final Color singlePieceBorderColor = Color.black;           // Border color of normal single pieces
    final Color gipfPieceBorderColor = new Color(0xDA0000);     // Border color of gipf pieces
    final Color positionNameColor = Color.red;                  // Color of position names
    final Color filledCircleColor = new Color(0xD4EEBD);        // Color of the circles that are filled
    final Color filledCircleBorderColor = new Color(0x7D8972);  // Border color of the filled circles

    // These mark the center hexagon on the obard
    Position[] centerCornerPositions = {            // Contains the corners of the center hexagon. Distinguishes the part where pieces can end up from the background
            new Position('b', 2),
            new Position('b', 5),
            new Position('e', 8),
            new Position('h', 5),
            new Position('h', 2),
            new Position('e', 2)
    };

    Position[] topAndBottomPositions = {
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

    Position[] sidePositions = {
            new Position('a', 2),
            new Position('a', 3),
            new Position('a', 4),
            new Position('i', 2),
            new Position('i', 3),
            new Position('i', 4)
    };

    // These positions are named on the board
    Position[] namedPositionsOnBoard = topAndBottomPositions;

    // These positions have a bigger on their position
    // Code concatenates two arrays via streams, see http://stackoverflow.com/a/23188881
    Position[] filledCirclePositions = Stream.concat(Arrays.stream(topAndBottomPositions), Arrays.stream(sidePositions)).toArray(Position[]::new);

    /*
     * line sets are used for easier drawing of the lines which indicate how the player is allowed to move.
     * Each line set contains a start and endpoint of a line on the board. In addition to each of these two points a direction
     * is stored, in which the next (parallel) line can be found. The last number indicates how many times a parallel
     * line can be drawn.
     * Each set of parallel lines is divided into two, because the direction in which the points move changes halfway
     */
    LineSet[] lineSets = {
            new LineSet(new Position('a', 2), new Position('f', 1), Move.Direction.NORTH, Move.Direction.NORTH_EAST, 4),
            new LineSet(new Position('b', 6), new Position('i', 2), Move.Direction.NORTH_EAST, Move.Direction.NORTH, 3),
            new LineSet(new Position('d', 1), new Position('i', 2), Move.Direction.NORTH_WEST, Move.Direction.NORTH, 4),
            new LineSet(new Position('a', 2), new Position('h', 6), Move.Direction.NORTH, Move.Direction.NORTH_WEST, 3),
            new LineSet(new Position('b', 1), new Position('b', 6), Move.Direction.SOUTH_EAST, Move.Direction.NORTH_EAST, 4),
            new LineSet(new Position('f', 1), new Position('f', 8), Move.Direction.NORTH_EAST, Move.Direction.SOUTH_EAST, 3)
    };
    GipfBoard gipfBoard;


    /**
     * Creates a component in which a Gipf board can be shown. Only works for standard sized boards
     *
     * @param gipfBoard
     */
    public GipfBoardComponent(GipfBoard gipfBoard) {
        this.gipfBoard = gipfBoard;

        setPreferredSize(new Dimension(600, 600));
    }

    public static void main(String argv[]) {
        GipfBoard gb = new GipfBoard();

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
        frame.getContentPane().add(new GipfBoardComponent(gb));

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Main paint class, from here all the other methods that paint something are called
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Set anti aliasing. Makes the drawing slightly slower, but look nicer
        // can be removed for performance.
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        paintBoard(g2);
        paintPieces(g2);
        if (drawFilledCircles) {
            paintFilledCircles(g2);
        }
        drawPositionNames(g2);
    }

    /**
     * Paints the board itself, including the background, center part, and the lines. This method is usually called from
     * the paintComponent method.
     *
     * @param g2
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
            g2.drawString(position.getName(), positionToScreenX(position), positionToScreenY(position));
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
        int colNumber = p.getColName() - 'a' + 1;               // Colnumber, starting at 1
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

    private class LineSet {
        Position start;
        Position end;
        Move.Direction nextStart;
        Move.Direction nextEnd;
        int nr;

        public LineSet(Position start, Position end, Move.Direction nextStart, Move.Direction nextEnd, int nr) {
            this.start = start;
            this.end = end;
            this.nextStart = nextStart;
            this.nextEnd = nextEnd;
            this.nr = nr;
        }
    }
}
