package GUI2.StringConverters;

import javafx.util.StringConverter;

import java.lang.reflect.Field;

/**
 * Created by frans on 20-11-2015.
 */
public class HeuristicStringConverter extends StringConverter<Field> {
    @Override
    public String toString(Field field) {
        return field.getName();
    }

    @Override
    public Field fromString(String string) {
        // TODO: Is this required if the field is non-editable?
        return null;
    }
}
