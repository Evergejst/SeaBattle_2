import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoggingGame {

    public void logMove(PrintWriter someWriter, String playerName, int x, int y, String result) {
        if (someWriter != null) {
            someWriter.printf("Move by %s at %s: (%d, %c) - %s%n", playerName, getCurrentTime(), x + 1, (char) ('A' + y), result);
            someWriter.flush();
        }
    }

    public void logGameEnd(PrintWriter logWriter, Player winner, Player loser, long startTime) {
        if (logWriter != null) {
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;

            logWriter.println();
            logWriter.println("Game ended at " + getCurrentTime());
            logWriter.printf("Duration: %d seconds%n", duration);
            logWriter.printf("Winner: %s%n", winner.getName());
            logWriter.printf("Loser: %s%n", loser.getName());
            logWriter.println("Final Boards:");
            logWriter.printf("Winner (%s) Board:%n", winner.getName());
            logWriter.println(winner.getBoard().drawBoardForLog());
            logWriter.printf("Loser 2 (%s) Board:%n", loser.getName());
            logWriter.println(loser.getBoard().drawBoardForLog());
            logWriter.println();

            logWriter.close();
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
