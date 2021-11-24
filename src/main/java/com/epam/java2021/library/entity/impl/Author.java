package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.ModifiableEntity;

import java.util.Calendar;

public class Author extends ModifiableEntity {
    private static final long serialVersionUID = 1L;

    private String name;

    private Author(long id, Calendar created, String name) {
        super(id, created);
        this.name = name;
    }

    public static class Builder {
        private long id;
        private Calendar modified;
        private String name;

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

        public Author build() {
            return new Author(id, modified, name);
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
                ", id=" + id +
                ", created=" + modified +
                ", name='" + name + '\'' +
                '}';
    }
}
