package com.epam.java2021.library.controller.listener;

import com.epam.java2021.library.dao.LangDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.task.AbstractPeriodicTask;
import com.epam.java2021.library.service.task.TaskScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;
import static com.epam.java2021.library.constant.ServletAttributes.*;

/**
 * Used for initialize crucial components for application:
 * <ul>
 *     <li> put all supported user roles to application context
 *     <li> get supported languages from DB and put them to app context
 *     <li> get default language from web.xml and put it to app context
 *     <li> get periodic tasks from web.xml, schedule it, and destroy them in case of shut down
 * </ul>
 */
public class ContextListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(ContextListener.class);
    private static final String INIT_PARAMETER_TASK = "TASK";
    private static final String INIT_PARAMETER_DEFAULT_LANG = "DEFAULT_LANG";
    private static final String DELIM = " ";

    /**
     * This should stop all scheduled task to shut down gracefully
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Servlet context destruction init...");
        TaskScheduler scheduler = TaskScheduler.getInstance();
        scheduler.cancelAll();
        logger.debug("Servlet context destruction finished");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.debug("Servlet context initialization init...");

        ServletContext servletContext = event.getServletContext();
        initUserRoles(servletContext);
        initSupportedLanguages(servletContext);
        initScheduledTasks(servletContext);

        logger.debug("Servlet context initialization finished");
    }

    private void initUserRoles(ServletContext servletContext) {
        logger.debug(START_MSG);

        List<String> appRoles = new ArrayList<>();
        for (User.Role r: User.Role.values()) {
            if (r.equals(User.Role.UNKNOWN)) {
                continue;
            }
            appRoles.add(r.name().toLowerCase());
        }
        servletContext.setAttribute(APP_ROLES, appRoles);
        logger.trace("{}={}", APP_ROLES, appRoles);
        logger.debug(END_MSG);
    }

    private void initSupportedLanguages(ServletContext servletContext) {
        logger.debug(START_MSG);

        String defaultLang = servletContext.getInitParameter(INIT_PARAMETER_DEFAULT_LANG);
        LangDao dao = DaoFactoryCreator.getDefaultFactory().newInstance().getLangDao();
        try {
            List<Lang> list = dao.getAll();

            for (Lang lang: list) {
                if (lang.getCode().equals(defaultLang)) {
                    servletContext.setAttribute(DEFAULT_LANG, lang);
                    logger.info("default language initialized: {}", lang);
                    break;
                }
            }

            servletContext.setAttribute(SUPPORTED_LANGUAGES, list);
            logger.info("Supported languages initialized.");
            logger.trace("{}={}", SUPPORTED_LANGUAGES, list);
        } catch (DaoException e) {
            logger.error("Unable to load supported languages: {}", e.getMessage());
        }
        logger.debug(END_MSG);
    }

    private void initScheduledTasks(ServletContext servletContext) {
        logger.debug(START_MSG);

        TaskScheduler scheduler = TaskScheduler.getInstance();

        String taskInit = servletContext.getInitParameter(INIT_PARAMETER_TASK);
        if (taskInit == null) {
            logger.info("No tasks specified");
            logger.debug(END_MSG);
            return;
        }

        for (String task: taskInit.split(DELIM)) {
            scheduleTask(servletContext, scheduler, task);
        }

        logger.debug(END_MSG);
    }

    private void scheduleTask(ServletContext servletContext, TaskScheduler scheduler, String task) {
        logger.trace("proceed task={}", task);
        String taskExecutionPeriod = servletContext.getInitParameter(task);
        if (taskExecutionPeriod == null || taskExecutionPeriod.isEmpty()) {
            logger.fatal("No execution period is specified for task {}. It'll be ignored", task);
            return;
        }

        try {
            long period = Long.parseLong(taskExecutionPeriod);

            Class<AbstractPeriodicTask> taskClass = (Class<AbstractPeriodicTask>) Class.forName(task);
            Constructor<AbstractPeriodicTask> taskClassConstructor = taskClass.getConstructor();
            AbstractPeriodicTask taskInstance = taskClassConstructor.newInstance();
            taskInstance.init(servletContext);

            scheduler.proceed(taskInstance, period);
            logger.info("{} will be executed every {} milliseconds", task, period);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            logger.fatal("Unable to instantiate task class {}: ", e.getMessage());
        } catch (NumberFormatException e) {
            logger.fatal("Error in task {} configuration: {}. Period should be valid long number",
                    task, e.getMessage());
        } catch (ServiceException e) {
            logger.fatal(e.getMessage());
        }
    }
}
