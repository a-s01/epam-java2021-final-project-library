package com.epam.java2021.library.jstltag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Enumeration;
import java.util.function.Function;

public class Debug extends TagSupport {
    private static final Logger logger = LogManager.getLogger(Debug.class);

    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = pageContext.getSession();
        JspWriter out = pageContext.getOut();

        try {
            out.write("<div class='container font-monospace'><hr />");

            out.write("<h3>Session</h3>");
            sessionPrintOut(out, session);

            out.write("<h3>Request Params</h3>");
            printOut(out, req.getParameterNames(), req::getParameter, true);

            out.write("<h3>Request Attributes</h3>");
            printOut(out, req.getAttributeNames(), req::getAttribute, true);

            out.write("</div>");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return SKIP_BODY;
    }

    private <T> void printOut(JspWriter out, Enumeration<String> attributeNames, Function<String, T> f, boolean newTable)
            throws IOException {
        if (newTable) {
            out.write("<table class=\"table table-sm table-warning\"><thead><tr><th>Attribute</th><th>Value</th></tr" +
                    "></thead><tbody>");
        }
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            out.write("<tr><td>" + attrName + "</td><td>" + f.apply(attrName) + "</td></tr>");
        }
        out.write("</tbody></table>");
    }

    private void sessionPrintOut(JspWriter out, HttpSession session) throws IOException {
        out.write("<table class=\"table table-sm table-warning\"><thead><tr><th>Attribute</th><th>Value</th></tr" +
                "></thead><tbody>");
        out.write("<tr><td>sessionID</td><td>" + session.getId() + "</td></tr>");
        out.write("<tr><td>isNew</td><td>" + session.isNew() + "</td></tr>");
        printOut(out, session.getAttributeNames(), session::getAttribute, false);
    }
}
