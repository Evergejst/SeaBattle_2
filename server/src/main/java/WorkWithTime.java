import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkWithTime {

    private long startTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getCurrentTime() {
        String currentTIme = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return currentTIme;
    }
}
