import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс WorkWithTime предоставляет методы для работы с временем.
 */
public class WorkWithTime {

    private long startTime;

    /**
     * Метод для получения времени начала работы.
     * @return Время начала работы в миллисекундах.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Метод для установки времени начала работы.
     * @param startTime Время начала работы в миллисекундах.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Метод для получения текущего времени в формате "yyyy-MM-dd HH:mm:ss".
     * @return Текущее время в формате строки.
     */
    public String getCurrentTime() {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return currentTime;
    }
}

