package GUI;

import GameLogic.Game;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;

/**
 * The GipfWindow uses a GipfBoardComponent to show the board. More information about the game can be added to the window
 * <p/>
 * Created by frans on 18-9-2015.
 */
class GipfWindow extends JFrame {
    private Game game;
    private GipfBoardComponent gipfBoardComponent;
    private JTextField newPieceCoordinateTextField;
    private JButton newPieceCoordinateEnterButton;
    private JTextArea debugTextArea;
    private JComboBox<Game.Piece> pieceTypeComboBox;

    private GipfWindow() throws HeadlessException {
        super();

        // Initialize the fields
        final JPanel contentPane = new JPanel();
        newPieceCoordinateTextField = new JTextField();
        newPieceCoordinateEnterButton = new JButton("Enter");
        game = new Game();
        gipfBoardComponent = new GipfBoardComponent(game);
        debugTextArea = new JTextArea("Debug information\n");
        pieceTypeComboBox = new JComboBox<>(Game.Piece.values());

        // Set the properties of the elements
        debugTextArea.setRows(10);
        gipfBoardComponent.addMouseListener(gipfBoardComponent);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GIPF");

        getContentPane().add(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));    // Put everything in a column

        contentPane.add(new JLabel("The GIPF game"));
        contentPane.add(gipfBoardComponent);

        contentPane.add(new JLabel("Enter coordinates for a new piece. (For example a2)"));
        contentPane.add(newPieceCoordinateTextField);
        contentPane.add(pieceTypeComboBox);
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

            if (game.isPositionOnBoard(newPiecePosition)) {
                Game.Piece pieceType = (Game.Piece) pieceTypeComboBox.getModel().getSelectedItem();

                addDebugInfo("Placing new " + pieceType + " at " + newPiecePosition.getName());

                game.getGipfBoard().getPieceMap().put(newPiecePosition, pieceType);
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
