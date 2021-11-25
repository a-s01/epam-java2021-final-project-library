package com.epam.java2021.library.controller.filter;

import com.epam.java2021.library.constant.Common;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.SafeContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static com.epam.java2021.library.constant.ServletAttributes.*;

public class LangDetection implements Filter {
    private static final Logger logger = LogManager.getLogger(LangDetection.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        logger.debug(Common.START_MSG);

        try {
            detectLanguage((HttpServletRequest) servletRequest);
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        logger.debug(Common.END_MSG);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void detectLanguage(HttpServletRequest req) throws ServiceException {
        SafeContext context = new SafeContext(req.getServletContext());
        HttpSession session = req.getSession();

        Lang lang = (Lang) session.getAttribute(LANG);
        if (lang != null) {
            logger.trace("found lang in session: {}", lang);
            return;
        }

        Enumeration<Locale> browserLocales = req.getLocales();
        logger.debug("try browser locales");
        while (browserLocales.hasMoreElements()) {
            Locale locale = browserLocales.nextElement();
            if (checkAndSetLang(context, session, locale.getLanguage()) != null) {
                return;
            }
        }

        Lang defaultLang = context.get(DEFAULT_LANG).notNull().convert(Lang.class::cast);
        session.setAttribute(LANG, defaultLang);
        logger.trace("fallback to default language: {}", defaultLang);
    }

    private Lang checkAndSetLang(SafeContext context, HttpSession session, String language) throws ServiceException {
        List<Lang> supported = context.get(SUPPORTED_LANGUAGES).notNull().convert(List.class::cast);
        for (Lang lang: supported) {
            if (lang.getCode().equals(language)) {
                session.setAttribute(LANG, lang);
                logger.trace("language set successfully: {}", lang);
                return lang;
            }
        }
        return null;
    }
}