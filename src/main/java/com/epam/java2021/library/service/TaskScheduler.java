package com.epam.java2021.library.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;

public class TaskScheduler {
    private static final Logger logger = LogManager.getLogger(TaskScheduler.class);
    private static final TaskScheduler INSTANCE = new TaskScheduler();

    public static TaskScheduler getInstance() {
        return INSTANCE;
    }
    public void proceed(TimerTask task, long period) {
        logger.info(START_MSG);
        Timer time = new Timer();
        time.schedule(task, 0, period);
        logger.info(END_MSG);
    }
}