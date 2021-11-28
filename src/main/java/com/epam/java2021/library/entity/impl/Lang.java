package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.Entity;

public class Lang extends Entity {
    private static final long serialVersionUID = 1L;
    private String code;

    private Lang(long id, String code) {
        super(id);
        this.code = code;
    }

    public static class Builder {
        private long id = -1;
        String code;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Lang build() {
            return new Lang(id, code);
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
        Lang language = (Lang) o;
        return code.equals(language.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "Lang{" +
                "id=" + id +
                ", code='" + code + '\'' +
                '}';
    }
}