import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
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
