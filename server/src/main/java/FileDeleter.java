import java.io.File;

public class FileDeleter {
    public static boolean deleteGame(String gameDirectory, String gameFileName) {
        File file = new File(gameDirectory, gameFileName);
        boolean isExist = file.exists() && file.delete();
        return isExist;
    }
}
