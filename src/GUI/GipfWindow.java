package GUI;

import GameLogic.GipfBoard;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GipfWindow uses a GipfBoardComponent to show the board. More information about the game can be added to the window
 *
 * Created by frans on 18-9-2015.
 */
class GipfWindow extends JFrame {
    private final JPanel contentPane;
    private GipfBoard gipfBoard;
    private GipfBoardComponent gipfBoardComponent;
    private JTextField newPieceCoordinateTextField;
    private JButton newPieceCoordinateEnterButton;

    private GipfWindow() throws HeadlessException {
        super();

        // Initialize the fields
        contentPane = new JPanel();
        newPieceCoordinateTextField = new JTextField();
        newPieceCoordinateEnterButton = new JButton("Enter");
        gipfBoard = new GipfBoard();
        gipfBoardComponent = new GipfBoardComponent(gipfBoard);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GIPF");

        getContentPane().add(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));    // Put everything in a column

        contentPane.add(new JLabel("The GIPF game"));
        contentPane.add(gipfBoardComponent);

        contentPane.add(new JLabel("Enter coordinates for a new piece. (For example a2)"));
        contentPane.add(newPieceCoordinateTextField);

        contentPane.add(newPieceCoordinateEnterButton);
        newPieceCoordinateEnterButton.addActionListener(e -> listenerAddNewPiece());


        contentPane.add(new JScrollPane(new JTextArea("Debug Info:\n")));

        pack();
        setVisible(true);
    }

    private void listenerAddNewPiece() {
        String newCoordinateText = newPieceCoordinateTextField.getText();
        newPieceCoordinateTextField.setText("");

        char colName = newCoordinateText.charAt(0);
        int rowNumber = Character.digit(newCoordinateText.charAt(1), 10);   // Convert the second character to a digit in base 10

        gipfBoard.getPieceMap().put(new Position(colName, rowNumber), GipfBoard.Piece.WHITE_SINGLE);
        gipfBoardComponent.repaint();
    }

    public static void main(String argv[]) {
        new GipfWindow();

    }
}
