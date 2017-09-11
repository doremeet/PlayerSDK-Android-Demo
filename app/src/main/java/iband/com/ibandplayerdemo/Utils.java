package iband.com.ibandplayerdemo;

import java.util.concurrent.TimeUnit;

/**
 * Created by yossibarel on 30/07/2017.
 */

public class Utils {
    public static String msToMinuteSec(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
