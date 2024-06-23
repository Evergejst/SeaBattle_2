import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Класс FileArchiver предназначен для архивирования файлов в формате ZIP.
 */
public class FileArchiver {

    /**
     * Архивирует указанный файл в заданной директории.
     * @param gameDirectory Директория, в которой находится файл для архивирования
     * @param gameFileName Имя файла для архивирования
     * @return true, если файл был успешно архивирован; false, если файл не найден или архивирование не удалось
     */
    public static boolean archiveGame(String gameDirectory, String gameFileName) {
        File file = new File(gameDirectory, gameFileName);
        if (!file.exists()) {
            return false;
        }

        try {
            String ARCHIVE_DIRECTORY = "FLKsajfklsdj"; // Директория для архивов (можно изменить по необходимости)
            File archiveDir = new File(ARCHIVE_DIRECTORY);
            if (!archiveDir.exists()) {
                archiveDir.mkdir();
            }

            String zipFileName = gameFileName.replace(".txt", ".zip");
            FileOutputStream fos = new FileOutputStream(new File(ARCHIVE_DIRECTORY, zipFileName));
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry(file.getName()));

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            zos.close();
            fis.close();
            fos.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
