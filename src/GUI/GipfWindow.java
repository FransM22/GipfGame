package GUI;

import GameLogic.GipfBoard;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;

/**
 * The GipfWindow uses a GipfBoardComponent to show the board. More information about the game can be added to the window
 * <p/>
 * Created by frans on 18-9-2015.
 */
class GipfWindow extends JFrame {
    private final JPanel contentPane;
    private GipfBoard gipfBoard;
    private GipfBoardComponent gipfBoardComponent;
    private JTextField newPieceCoordinateTextField;
    private JButton newPieceCoordinateEnterButton;
    private JTextArea debugTextArea;

    private GipfWindow() throws HeadlessException {
        super();

        // Initialize the fields
        contentPane = new JPanel();
        newPieceCoordinateTextField = new JTextField();
        newPieceCoordinateEnterButton = new JButton("Enter");
        gipfBoard = new GipfBoard();
        gipfBoardComponent = new GipfBoardComponent(gipfBoard);
        debugTextArea = new JTextArea("Debug information\n");

        // Set the properties of the elements
        debugTextArea.setRows(10);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GIPF");

        getContentPane().add(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));    // Put everything in a column

        contentPane.add(new JLabel("The GIPF game"));
        contentPane.add(gipfBoardComponent);

        contentPane.add(new JLabel("Enter coordinates for a new piece. (For example a2)"));
        contentPane.add(newPieceCoordinateTextField);

        contentPane.add(newPieceCoordinateEnterButton);

        // Add listeners
        newPieceCoordinateTextField.addActionListener(e -> listenerAddNewPiece());
        newPieceCoordinateEnterButton.addActionListener(e -> listenerAddNewPiece());


        contentPane.add(new JScrollPane(debugTextArea));

        pack();
        setVisible(true);
    }

    public static void main(String argv[]) {
        new GipfWindow();

    }

    private void listenerAddNewPiece() {
        String newCoordinateText = newPieceCoordinateTextField.getText();
        newPieceCoordinateTextField.setText("");
        newPieceCoordinateTextField.requestFocus();

        try {
            char colName = newCoordinateText.charAt(0);
            int rowNumber = Character.digit(newCoordinateText.charAt(1), 10);   // Convert the second character to a digit in base 10
            Position newPiecePosition = new Position(colName, rowNumber);

            if (gipfBoard.isPositionOnBoard(newPiecePosition)) {
                addDebugInfo("Placing new piece at " + newPiecePosition.getName());

                gipfBoard.getPieceMap().put(newPiecePosition, GipfBoard.Piece.WHITE_SINGLE);
                gipfBoardComponent.repaint();
            } else {
                addDebugInfo("Position " + newPiecePosition.getName() + " is invalid");
            }
        } catch (Exception e) {
            addDebugInfo("Can't parse '" + newCoordinateText + "'");
        }
    }

    private void addDebugInfo(String s) {
        debugTextArea.append(s + "\n");
    }
}
