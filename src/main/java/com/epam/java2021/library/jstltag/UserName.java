package com.epam.java2021.library.jstltag;

import com.epam.java2021.library.entity.impl.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class UserName extends TagSupport {
    private static final Logger logger = LogManager.getLogger(UserName.class);
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public int doStartTag() throws JspException {
        logger.trace("doStartTag init: user={}", user);
        JspWriter out = pageContext.getOut();
        String name = user.getName() != null ? user.getName() : user.getEmail();
        try {
            out.print(name);
        }catch(Exception e){e.printStackTrace();}

        return SKIP_BODY;
    }
}
