package GUI2;

import javafx.scene.control.Spinner;

/**
 * TODO
 * Created by frans on 21-11-2015.
 */
public class MilliSecondSpinner extends Spinner<Integer> {
    public MilliSecondSpinner(int low, int high, int initialValue, int step) {
        super(low, high, initialValue, step);
    }
}
