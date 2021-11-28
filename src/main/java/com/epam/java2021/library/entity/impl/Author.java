package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.ModifiableEntity;

import java.util.Calendar;
import java.util.List;

public class Author extends ModifiableEntity {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<I18AuthorName> i18Names;

    private Author(long id, Calendar created, String name, List<I18AuthorName> i18Names) {
        super(id, created);
        this.name = name;
        this.i18Names = i18Names;
    }

    public static class Builder {
        private long id = -1;
        private Calendar modified;
        private String name;
        private List<I18AuthorName> i18Names;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setModified(Calendar modified) {
            this.modified = modified;
            return this;
        }

        public void setI18Names(List<I18AuthorName> i18Names) {
            this.i18Names = i18Names;
        }

        public Author build() {
            return new Author(id, modified, name, i18Names);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<I18AuthorName> getI18Names() {
        return i18Names;
    }

    public void setI18Names(List<I18AuthorName> i18Names) {
        this.i18Names = i18Names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return name.equals(author.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", i18Names=" + i18Names +
                '}';
    }
}
