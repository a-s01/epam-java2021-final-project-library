package com.epam.java2021.library.jstltag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.List;

public class SetList extends TagSupport {
    private static final Logger logger = LogManager.getLogger(SetList.class);
    private String var;
    private String value;

    public void setVar(String var) {
        this.var = var;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int doStartTag() throws JspException {
        logger.trace("init: var={}, value={}", var, value);

        List<String> list = new ArrayList<>();
        for (String s: value.split("\\s+")) {
            list.add(s);
        }

        pageContext.setAttribute(var, list);

        return SKIP_BODY;
    }
}