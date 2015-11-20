package GUI2.StringConverters;

import GameLogic.GipfBoardState;
import GameLogic.Move;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * Created by frans on 20-11-2015.
 */
public class AlgorithmStringConverter extends StringConverter<Class<? extends Function<GipfBoardState, Move>>> {
    ObservableList<Class<? extends Function<GipfBoardState, Move>>> playerClassList;

    public AlgorithmStringConverter(ObservableList<Class<? extends Function<GipfBoardState, Move>>> playerOList) {
        this.playerClassList = playerOList;
    }

    @Override
    public String toString(Class<? extends Function<GipfBoardState, Move>> playerClass) {
        return playerClass.getSimpleName();
    }

    @Override
    public Class<? extends Function<GipfBoardState, Move>> fromString(String playerClassName) {
        for (Class<? extends Function<GipfBoardState, Move>> c : playerClassList) {
            if (playerClassName.equals(c.getSimpleName())) {
                return c;
            }
        }

        // If the class is not found
        System.err.println("Player " + playerClassName + " not found");
        return null;
    }
}
