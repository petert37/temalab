package measure;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vkrissz on 2016.12.13..
 */
public class TimeLogger {
    private static List<ElapsedTime> times = new ArrayList<>();
    private static ReschedulableTimer timer = new ReschedulableTimer();
    private static Runnable writerTask = new Runnable() {
        @Override
        public void run() {
            System.out.println(printResults());
            times.clear();
        }
    };

    public static void saveTime(ElapsedTime elapsedTime) {
        if (times.size() == 0)
            timer.schedule(writerTask, 2L * 60L * 1000L);
        else timer.reschedule(2L * 60L * 1000L);
        times.add(elapsedTime);
    }

    public static String printResults() {
        StringBuilder sb = new StringBuilder();
        for (ElapsedTime et : times)
            sb.append(et.toString()).append(System.lineSeparator());
        return sb.toString();
    }


    public static class ReschedulableTimer extends Timer {
        private Runnable task;
        private TimerTask timerTask;

        public void schedule(Runnable runnable, long delay) {
            task = runnable;
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            };
            this.schedule(timerTask, delay);
        }

        public void reschedule(long delay) {
            timerTask.cancel();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            };
            this.schedule(timerTask, delay);
        }
    }
}
