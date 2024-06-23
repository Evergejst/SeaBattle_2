import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

public class FormerListMoves {

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
