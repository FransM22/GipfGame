package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by frans on 18-9-2015.
 */
public class GipfBoardComponent extends JComponent {
    public GipfBoardComponent() {
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.blue);

        g2.fillRect(0, 0, 10, 20);
    }

    public static void main(String argv[]) {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new GipfBoardComponent());

        frame.pack();
        frame.setVisible(true);
    }
}
