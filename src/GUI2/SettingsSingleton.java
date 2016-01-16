package GUI2;

/**
 * Created by frans on 9-1-2016.
 */
public class SettingsSingleton {
    private static final SettingsSingleton instance = new SettingsSingleton();
    public boolean showExperimentOutput = false;
    public boolean showMCTSOutput = true;

    private SettingsSingleton() {
        if (showExperimentOutput) {
            System.out.println("White player; Black player; Number of moves; Time (ms); Winning player");
        }
    }

    public static SettingsSingleton getInstance() { return instance; }
}
