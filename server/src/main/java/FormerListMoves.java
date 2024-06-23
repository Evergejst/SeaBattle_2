import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс FormerListMoves предназначен для формирования списка ходов игры из журнала и их передачи через веб-сокет.
 */
public class FormerListMoves {

    /**
     * Формирует список ходов из журнала игры и отправляет его через веб-сокет.
     * @param conn WebSocket соединение, через которое отправляются ходы игры
     * @param gameFileName Имя файла с журналом игры
     */
    public static void formListMoves(WebSocket conn, String gameFileName) {
        // Считать журнал игры из файла
        List<String> gameLogs = FileReader.readGameLog(gameFileName);

        // Проверка на пустоту журнала
        if (gameLogs.isEmpty()) {
            System.out.println("No game log data found.");
            return; // Завершаем метод, если журнал пуст
        }

        // Разделение журнала на ходы
        List<List<String>> moves = parseMoves(gameLogs);

        // Преобразуем объект moves в строку
        String movesString = serializeMoves(moves);

        // Отправляем строку через веб-сокет
        conn.send(movesString);
    }

    /**
     * Преобразует список ходов в строковое представление.
     * @param moves Список ходов игры
     * @return Строковое представление всех ходов
     */
    private static String serializeMoves(List<List<String>> moves) {
        StringBuilder sb = new StringBuilder();
        for (List<String> move : moves) {
            for (String line : move) {
                sb.append(line).append("\n"); // Разделяем строки символом новой строки
            }
            sb.append("\n"); // Разделяем ходы пустой строкой
        }
        return sb.toString();
    }

    /**
     * Разделяет журнал игры на отдельные ходы.
     * @param gameLogs Список строк, содержащий журнал игры
     * @return Список ходов, каждый из которых представлен как список строк
     */
    private static List<List<String>> parseMoves(List<String> gameLogs) {
        List<List<String>> moves = new ArrayList<>();
        List<String> currentMove = new ArrayList<>();

        for (String line : gameLogs) {
            if (line.startsWith("Move by")) {
                if (!currentMove.isEmpty()) {
                    moves.add(new ArrayList<>(currentMove));
                    currentMove.clear();
                }
            }
            currentMove.add(line);
        }

        if (!currentMove.isEmpty()) {
            moves.add(currentMove);
        }

        return moves;
    }

}
