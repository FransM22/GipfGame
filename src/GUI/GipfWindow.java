package GUI;

import GameLogic.GipfBoard;

import javax.swing.*;
import java.awt.*;

/**
 * The GipfWindow uses a GipfBoardComponent to show the board. More information about the game can be added to the window
 *
 * Created by frans on 18-9-2015.
 */
class GipfWindow extends JFrame {
    private final JPanel contentPane;

    private GipfWindow() throws HeadlessException {
        super();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GIPF");

        contentPane = new JPanel();
        getContentPane().add(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));    // Put everything in a column

        GipfBoard gb = new GipfBoard();
        GipfBoardComponent gbc = new GipfBoardComponent(gb);
        contentPane.add(new JLabel("The GIPF game"));
        contentPane.add(gbc);

        contentPane.add(new JLabel("Enter coordinates for a new piece"));
        contentPane.add(new JTextField());

        contentPane.add(new JScrollPane(new JTextArea("Debug Info:\n")));

        pack();
        setVisible(true);
    }

    public static void main(String argv[]) {
        new GipfWindow();

    }
}
