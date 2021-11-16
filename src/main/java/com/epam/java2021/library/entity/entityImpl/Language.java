package com.epam.java2021.library.entity.entityImpl;

import com.epam.java2021.library.entity.Entity;

import java.util.Objects;

public class Language extends Entity {
    private static final long serialVersionUID = 1L;
    private String code;

    private Language(long id, String code) {
        super(id);
        this.code = code;
    }

    public static class Builder extends Entity.Builder {
        String code;

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Language build() {
            return new Language(id, code);
        }
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return code.equals(language.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "Language{" +
                "code='" + code + '\'' +
                '}';
    }
}