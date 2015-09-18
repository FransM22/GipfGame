package GUI;

import GameLogic.GipfBoard;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Created by frans on 18-9-2015.
 */
public class GipfBoardComponent extends JComponent {
    final int pieceSize = 50;
    final boolean displayPiecePosition = true;
    final int nrOfColumnsOnGipfBoard = 9;
    final int nrOfRowsOnGipfBoard = 9;
    final int marginSize = 20;
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

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0xeeeeee));
        g2.fillRect(0, 0, getWidth(), getHeight());

        paintBoard(g2);
        paintPieces(g2);
    }

    private void paintBoard(Graphics2D g2) {
        // Fill the board itself, this needs a polygon with 6 corners
        g2.setColor(Color.white);
        Position corner1 = new Position(22);    // b2
        Position corner2 = new Position(25);    // b5
        Position corner3 = new Position(58);    // e8
        Position corner4 = new Position(88);    // h5
        Position corner5 = new Position(85);    // h2
        Position corner6 = new Position(52);    // e2

        g2.fillPolygon(
                new int[]{positionToScreenX(corner1), positionToScreenX(corner2), positionToScreenX(corner3), positionToScreenX(corner4), positionToScreenX(corner5), positionToScreenX(corner6),},
                new int[]{positionToScreenY(corner1), positionToScreenY(corner2), positionToScreenY(corner3), positionToScreenY(corner4), positionToScreenY(corner5), positionToScreenY(corner6),},
                6
        );

        // Draw the lines
        g2.setColor(Color.gray);

        // Draw the lines between a2 - g1 and a5 - i1
        for (int i = 0; i < 4; i++) {
            Position start = new Position(12 + i);      // 12 is the id of a2
            Position end = new Position(62 + i * 11);   // 62 is the id of g1
            g2.drawLine(
                    positionToScreenX(start),
                    positionToScreenY(start),
                    positionToScreenX(end),
                    positionToScreenY(end)
            );
        }

        // Draw the lines between b6 - g6 and d8 - i4
        for (int i = 0; i < 3; i++) {
            Position start = new Position(26 + i * 11);      // 26 is the id of b6
            Position end = new Position(96 + i);         // 96 is the id of i2
            g2.drawLine(
                    positionToScreenX(start),
                    positionToScreenY(start),
                    positionToScreenX(end),
                    positionToScreenY(end)
            );
        }

        // Draw the lines between d1 - i2 and a1 - i5
        for (int i = 0; i < 4; i++) {
            Position start = new Position(41 - i * 10);      // 41 is the id of d1
            Position end = new Position(96 + i);             // 96 is the id of i2
            g2.drawLine(
                    positionToScreenX(start),
                    positionToScreenY(start),
                    positionToScreenX(end),
                    positionToScreenY(end)
            );
        }

        // Draw the lines between a2 - h6 and a4 - f8
        for (int i = 0; i < 3; i++) {
            Position start = new Position(12 + i);             // 12 is the id of a2
            Position end = new Position(89 - i * 10);          // 89 is the id of h6
            g2.drawLine(
                    positionToScreenX(start),
                    positionToScreenY(start),
                    positionToScreenX(end),
                    positionToScreenY(end)
            );
        }

        // Draw the lines between b1 - b6 and e1 - e9
        for (int i = 0; i < 4; i++) {
            Position start = new Position(21 + i * 10);          // 21 is the id of b1
            Position end = new Position(26 + i * 11);            // 26 is the id of b6
            g2.drawLine(
                    positionToScreenX(start),
                    positionToScreenY(start),
                    positionToScreenX(end),
                    positionToScreenY(end)
            );
        }

        // Draw the lines between f1 - f8 and h1 - h6
        for (int i = 0; i < 3; i++) {
            Position start = new Position(62 + i * 11);          // 62 is the id of g1
            Position end = new Position(69 + i * 10);            // 68 is the id of g8
            g2.drawLine(
                    positionToScreenX(start),
                    positionToScreenY(start),
                    positionToScreenX(end),
                    positionToScreenY(end)
            );
        }
    }

    private void centerCircleOn(Graphics2D g2, int x, int y, int size, Color fillColor, Color borderColor) {
        g2.setColor(fillColor);
        g2.fillOval(
                x - (size / 2),
                y - (size / 2),
                size,
                size
        );

        g2.setColor(borderColor);
        g2.drawOval(
                x - (size / 2),
                y - (size / 2),
                size,
                size
        );
    }

    private void drawPiece(Graphics2D g2, Position position, GipfBoard.Piece piece) {
        Color fillColor;
        Color borderColor;

        switch (piece) {
            case WHITE_SINGLE:
                fillColor = Color.white;
                borderColor = Color.lightGray;
                break;
            case WHITE_GIPF:
                fillColor = Color.white;
                borderColor = Color.red;
                break;
            case BLACK_SINGLE:
                fillColor = Color.darkGray;
                borderColor = Color.lightGray;
                break;
            case BLACK_GIPF:
                fillColor = Color.darkGray;
                borderColor = Color.red;
                break;
            default:
                fillColor = Color.pink;
                borderColor = Color.pink;
        }
        centerCircleOn(g2, positionToScreenX(position), positionToScreenY(position), pieceSize, fillColor, borderColor);

        if (displayPiecePosition) {
            g2.setColor(Color.blue);
            g2.drawString(position.toString(), positionToScreenX(position), positionToScreenY(position));
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
}
