package com.epam.java2021.library.service.util;

import com.epam.java2021.library.exception.ServiceException;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

public class Captcha implements Serializable {
    private static final Logger logger = LogManager.getLogger(Captcha.class);
    private static final long serialVersionUID = 1L;
    private static final Cage cage = new GCage();

    private final String token;
    private boolean used;
    private String base64Image;

    public Captcha() throws ServiceException {
        token = cage.getTokenGenerator().next();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            cage.draw(token, output);
            byte[] bytes = output.toByteArray();
            base64Image = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            logger.error("error in captcha generation: {}", e.getMessage());
            throw new ServiceException("error.in.captcha.generation", e.getMessage());
        }
    }

    public String getToken() {
        return token;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
        if (used) {
            base64Image = "";
        }
    }

    public String getBase64Image() {
        return base64Image;
    }

    @Override
    public String toString() {
        return "Captcha{" +
                "token='" + token + '\'' +
                ", used=" + used +
                ", base64Image='" + base64Image + '\'' +
                '}';
    }
}
