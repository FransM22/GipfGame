import java.util.concurrent.TimeUnit;

/**
 * Created by frans on 7-9-2015.
 */
public class Game {
    public static void main(String argv[]) {
        System.out.println("Gipf game started");

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("Game ended. The computer won.");
    }
}
