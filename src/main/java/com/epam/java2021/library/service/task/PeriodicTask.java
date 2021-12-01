package com.epam.java2021.library.service.task;

import com.epam.java2021.library.exception.ServiceException;

import javax.servlet.ServletContext;

public interface PeriodicTask {
    void init(ServletContext context) throws ServiceException;
}
