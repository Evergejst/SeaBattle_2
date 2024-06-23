import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс FileReader предназначен для чтения данных из файлов.
 */
public class FileReader {

    /**
     * Читает все строки из указанного файла.
     * @param filePath Путь к файлу, который нужно прочитать
     * @return Список строк из файла
     */
    public static List<String> readGameLog(String filePath) {
        List<String> gameLogs = new ArrayList<>();
        try {
            gameLogs = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading game log: " + e.getMessage());
        }
        return gameLogs;
    }
}
