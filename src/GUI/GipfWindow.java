package GUI;

import GUI.GipfBoardComponent.GipfBoardComponent;
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
    final JTextArea debugTextArea;
    private final GipfBoardComponent gipfBoardComponent;
    private final Game game;
    private final JTextField newPieceCoordinateTextField;
    private final JComboBox<Game.Piece> pieceTypeComboBox;
    private final JLabel piecesLeftLabel;
    private final JLabel currentPlayerLabel;

    private GipfWindow() throws HeadlessException {
        super();

        // Initialize the fields
        final JPanel contentPane = new JPanel();
        newPieceCoordinateTextField = new JTextField();
        JButton newPieceCoordinateEnterButton = new JButton("Enter");
        JButton previousStateButton = new JButton("Undo");
        game = new Game();
        gipfBoardComponent = new GipfBoardComponent(game);
        debugTextArea = new DebugTextArea();
        pieceTypeComboBox = new JComboBox<>(Game.Piece.values());
        piecesLeftLabel = new JLabel(" ");
        currentPlayerLabel = new JLabel(" ");
        GameStateUpdater gameStateUpdater = new GameStateUpdater(this, game);
        JMenuBar menubar = new JMenuBar();
        JMenu newGameMenu = new JMenu("New game");
        JMenuItem newBasicGameMenuItem = new JMenuItem("Basic game");
        JMenuItem newStandardGameMenuItem = new JMenuItem("Standard game");
        JMenuItem newTournamentGameMenuItem = new JMenuItem("Tournament game");

        menubar.add(newGameMenu);
        newGameMenu.add(newBasicGameMenuItem);
        newGameMenu.add(newStandardGameMenuItem);
        newGameMenu.add(newTournamentGameMenuItem);

        // Set the properties of the elements
        debugTextArea.setRows(10);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GIPF");

        getContentPane().add(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));    // Put everything in a column

        setJMenuBar(menubar);
        contentPane.add(new JLabel("The GIPF game"));
        contentPane.add(currentPlayerLabel);
        contentPane.add(piecesLeftLabel);
        contentPane.add(gipfBoardComponent);
        contentPane.add(previousStateButton);

//        contentPane.add(new JLabel("Enter coordinates for a new piece. (For example a2)"));
//        contentPane.add(newPieceCoordinateTextField);
//        contentPane.add(pieceTypeComboBox);
//        contentPane.add(newPieceCoordinateEnterButton);

        // Add listeners
        newPieceCoordinateTextField.addActionListener(e -> listenerAddNewPiece());
        newPieceCoordinateEnterButton.addActionListener(e -> listenerAddNewPiece());
        previousStateButton.addActionListener(e -> returnToPreviousState());


        contentPane.add(new JScrollPane(debugTextArea));

        pack();
        setVisible(true);
        new Thread(gameStateUpdater).run();
    }

    public static void main(String argv[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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

            if (game.isPositionOnBigBoard(newPiecePosition)) {
                Game.Piece pieceType = (Game.Piece) pieceTypeComboBox.getModel().getSelectedItem();

                debugTextArea.append("Placing new " + pieceType + " at " + newPiecePosition.getName());

                game.getGipfBoardState().getPieceMap().put(newPiecePosition, pieceType);
                gipfBoardComponent.repaint();
            } else {
                debugTextArea.append("Position " + newPiecePosition.getName() + " is invalid");
            }
        } catch (Exception e) {
            debugTextArea.append("Can't parse '" + newCoordinateText + "'");
        }
    }

    private void returnToPreviousState() {
        game.returnToPreviousBoard();
        gipfBoardComponent.repaint();
    }

    public void setPiecesLeftLabel(String message) {
        piecesLeftLabel.setText(message);
    }

    public void setCurrentPlayerLabel(String message) {
        currentPlayerLabel.setText(message);
    }
}
