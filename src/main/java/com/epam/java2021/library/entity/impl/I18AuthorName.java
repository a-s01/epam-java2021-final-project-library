package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.Entity;

import java.util.Objects;

public class I18AuthorName extends Entity {
    private static final long serialVersionUID = 1L;

    private long langId;
    private String name;

    private I18AuthorName(long id, long langId, String name) {
        super(id);
        this.langId = langId;
        this.name = name;
    }

    public static class Builder {
        private long id;
        private long langId;
        private String name;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setLangId(long langId) {
            this.langId = langId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public I18AuthorName build() {
            return new I18AuthorName(id, langId, name);
        }
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
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
        I18AuthorName that = (I18AuthorName) o;
        return langId == that.langId && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(langId, name);
    }

    @Override
    public String toString() {
        return "I18AuthorName{" +
                "id=" + id +
                ", langId=" + langId +
                ", name='" + name + '\'' +
                '}';
    }
}
