package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by frans on 29-9-2015.
 */
class DebugTextArea extends JTextArea {
    public DebugTextArea() {
        setFont(Font.decode("Monospaced-11"));
    }

    public void append(String message) {
        super.append(message + "\n");
        setCaretPosition(getDocument().getLength());
    }
}
