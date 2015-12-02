package GUI2.StringConverters;

import AI.Players.ComputerPlayer;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

/**
 * Created by frans on 20-11-2015.
 */
public class AlgorithmStringConverter extends StringConverter<Class<? extends ComputerPlayer>> {
    ObservableList<Class<? extends ComputerPlayer>> playerClassList;

    public AlgorithmStringConverter(ObservableList<Class<? extends ComputerPlayer>> playerOList) {
        this.playerClassList = playerOList;
    }

    @Override
    public String toString(Class<? extends ComputerPlayer> playerClass) {
        return playerClass.getSimpleName();
    }

    @Override
    public Class<? extends ComputerPlayer> fromString(String playerClassName) {
        for (Class<? extends ComputerPlayer> c : playerClassList) {
            if (playerClassName.equals(c.getSimpleName())) {
                return c;
            }
        }

        // If the class is not found
        System.err.println("Player " + playerClassName + " not found");
        return null;
    }
}
