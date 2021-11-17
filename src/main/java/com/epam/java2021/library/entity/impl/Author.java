package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.EditableEntity;

import java.sql.Date;

public class Author extends EditableEntity {
    private static final long serialVersionUID = 1L;

    private String name;

    private Author(long id, Date created, EditRecord lastEdit, String name) {
        super(id, created, lastEdit);
        this.name = name;
    }

    public static class Builder extends EditableEntity.Builder {
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Author build() {
            return new Author(id, created, lastEdit, name);
        }
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
                "lastEdit=" + lastEdit +
                ", id=" + id +
                ", created=" + created +
                ", name='" + name + '\'' +
                '}';
    }
}
