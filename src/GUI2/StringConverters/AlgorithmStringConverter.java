package GUI2.StringConverters;

import GameLogic.GipfBoardState;
import GameLogic.Move;
import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * Created by frans on 20-11-2015.
 */
public class AlgorithmStringConverter extends StringConverter<Class<? extends Function<GipfBoardState, Move>>> {
    @Override
    public String toString(Class<? extends Function<GipfBoardState, Move>> playerClass) {
        return playerClass.getSimpleName();
    }

    @Override
    public Class<? extends Function<GipfBoardState, Move>> fromString(String string) {
        // TODO: Is this required in a non-editable combobox?
        return null;
    }
}
