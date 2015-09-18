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
    GipfBoard gipfBoard;

    public GipfBoardComponent(GipfBoard gipfBoard) {
        this.gipfBoard = gipfBoard;

        setPreferredSize(new Dimension(800, 600));
    }

    public static void main(String argv[]) {
        GipfBoard gb = new GipfBoard();
        gb.setPiece(new Position('a', 1), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 3), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 4), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 5), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 6), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 7), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 8), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('a', 9), GipfBoard.Piece.WHITE_SINGLE);

        gb.setPiece(new Position('a', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('b', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('c', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('d', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('e', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('f', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('g', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('h', 2), GipfBoard.Piece.WHITE_SINGLE);
        gb.setPiece(new Position('i', 2), GipfBoard.Piece.WHITE_SINGLE);

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new GipfBoardComponent(gb));

        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        paintBoard(g2);
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
            case BLACK_GIPF:
                fillColor = Color.darkGray;
                borderColor = Color.red;
                break;
            default:
                fillColor = Color.pink;
                borderColor = Color.pink;
        }
        centerCircleOn(g2, positionToScreenX(position), positionToScreenY(position), 10, fillColor, borderColor);
    }

    private void paintBoard(Graphics2D g2) {


        for (Map.Entry<Position, GipfBoard.Piece> entry : gipfBoard.getPieceMap().entrySet()) {
            Position position = entry.getKey();
            GipfBoard.Piece piece = entry.getValue();

            drawPiece(g2, position, piece);
        }
    }

    private int positionToScreenY(Position p) {
        int height = getHeight();

        return height - (p.getRowNumber() - 1) * (height / 8);
    }

    private int positionToScreenX(Position p) {
        return (p.getColName() - 'a') * (getWidth() / 8);
    }
}
