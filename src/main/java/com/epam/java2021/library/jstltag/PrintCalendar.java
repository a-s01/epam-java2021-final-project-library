package com.epam.java2021.library.jstltag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintCalendar extends TagSupport {
    private static final Logger logger = LogManager.getLogger(PrintCalendar.class);

    private String format;
    private Calendar calendar;

    public void setFormat(String format) {
        this.format = format;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public int doStartTag() throws JspException {
        logger.trace("init: formatStr={}, calendar={}", calendar);

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        JspWriter out = pageContext.getOut();
        try {
            out.print(sdf.format(calendar.getTime()));
        } catch(Exception e) {
            logger.error(e.getMessage());

        }

        return SKIP_BODY;
    }
}
