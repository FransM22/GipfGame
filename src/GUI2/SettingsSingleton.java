package GUI2;

/**
 * Created by frans on 9-1-2016.
 */
public class SettingsSingleton {
    private static final SettingsSingleton instance = new SettingsSingleton();
    public boolean showMoveCountAtGameEnd = true;
    public boolean showTimeAtGameEnd = true;
    public boolean showWinner = true;
    public boolean showWhiteAlgorithm = true;
    public boolean showBlackAlgorithm = true;

    private SettingsSingleton() {
        System.out.println("White player; Black player; Number of moves; Time (ms); Winning player");
    }

    public static SettingsSingleton getInstance() { return instance; }
}
