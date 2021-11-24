package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.Entity;

public class AuthorNames extends Entity {
    private static final long serialVersionUID = 1L;

    private long lang_id;
    private String name;

    private AuthorNames(long id, long lang_id, String name) {
        super(id);
        this.lang_id = lang_id;
        this.name = name;
    }
/*
    public static class Builder {
        private long id;
        private long lang_id;
        private String name;

        public Builder setLang_id(long lang_id) {
            this.lang_id = lang_id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public AuthorNames build() {
            return new AuthorNames(id, lang_id, name);
        }
    }

    public long getLang_id() {
        return lang_id;
    }

    public void setLang_id(long lang_id) {
        this.lang_id = lang_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorNames that = (AuthorNames) o;
        return id == that.id && lang_id == that.lang_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lang_id);
    }

    @Override
    public String toString() {
        return "AuthorNames{" +
                "id=" + id +
                ", lang_id=" + lang_id +
                ", name='" + name + '\'' +
                '}';
    }

     */
}
