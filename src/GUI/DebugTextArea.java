package GUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by frans on 29-9-2015.
 */
public class DebugTextArea extends JTextArea {
    public DebugTextArea() {
        setFont(Font.decode("Monospaced-11"));
    }

    public void append(String message) {
        String timeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("[HH:mm:ss.SSS]"));
        super.append(timeString + ": " + message + "\n");
        setCaretPosition(getDocument().getLength());
    }
}
