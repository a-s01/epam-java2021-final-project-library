package com.epam.java2021.library.service.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;

public class TaskScheduler {
    private static final Logger logger = LogManager.getLogger(TaskScheduler.class);
    private static final TaskScheduler INSTANCE = new TaskScheduler();
    private static final HashSet<Timer> timers = new HashSet<>();

    public static TaskScheduler getInstance() {
        return INSTANCE;
    }
    public void proceed(TimerTask task, long period) {
        logger.info(START_MSG);
        logger.debug("task={}, period={}", task, period);
        Timer time = new Timer();
        time.schedule(task, 0, period);

        timers.add(time);
        logger.info(END_MSG);
    }

    public void cancelAll() {
        logger.info("begin to cancel tasks");
        for (Timer t: timers) {
            t.cancel();
        }
        logger.info("done");
    }
}