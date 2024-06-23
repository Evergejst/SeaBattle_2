import java.io.File;

/**
 * Класс FileDeleter предназначен для удаления файлов.
 */
public class FileDeleter {

    /**
     * Удаляет указанный файл из заданной директории.
     * @param gameDirectory Директория, в которой находится файл
     * @param gameFileName Имя файла для удаления
     * @return true, если файл был успешно удален; false, если файл не найден или удаление не удалось
     */
    public static boolean deleteGame(String gameDirectory, String gameFileName) {
        File file = new File(gameDirectory, gameFileName);
        return file.exists() && file.delete();
    }
}
