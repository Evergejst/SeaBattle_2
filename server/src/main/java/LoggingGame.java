import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс LoggingGame предназначен для логирования ходов игры и её завершения.
 */
public class LoggingGame {

    /**
     * Записывает информацию о ходе игрока в лог.
     * @param someWriter Писатель для записи лога
     * @param playerName Имя игрока
     * @param x Координата X выстрела
     * @param y Координата Y выстрела
     * @param result Результат выстрела
     */
    public void logMove(PrintWriter someWriter, String playerName, int x, int y, String result) {
        if (someWriter != null) {
            someWriter.printf("Move by %s at %s: (%d, %c) - %s%n", playerName, getCurrentTime(), x + 1, (char) ('A' + y), result);
            someWriter.flush();
        }
    }

    /**
     * Записывает информацию о завершении игры в лог.
     * @param logWriter Писатель для записи лога
     * @param winner Победитель игры
     * @param loser Проигравший игрок
     * @param startTime Время начала игры
     */
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
            logWriter.printf("Loser (%s) Board:%n", loser.getName());
            logWriter.println(loser.getBoard().drawBoardForLog());
            logWriter.println();

            logWriter.close();
        }
    }

    /**
     * Получает текущее время в формате "yyyy-MM-dd HH:mm:ss".
     * @return Текущее время в строковом представлении
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
