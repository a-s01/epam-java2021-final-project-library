package com.epam.java2021.library.entity.entityImpl;

import com.epam.java2021.library.entity.EditableEntity;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Author extends EditableEntity {
    private static final long serialVersionUID = 1L;

    private HashMap<Integer, String> names;

    private Author(long id, LocalDateTime created, EditRecord lastEdit, HashMap<Integer, String> names) {
        super(id, created, lastEdit);
        this.names = names;
    }

    public static class Builder extends EditableEntity.Builder {
        private HashMap<Integer, String> names = new HashMap<>();

        public Builder setNames(HashMap<Integer, String> names) {
            this.names = names;
            return this;
        }

        public Builder addName(int lang, String name) {
            names.put(lang, name);
            return this;
        }

        public Author build() {
            return new Author(id, created, lastEdit, names);
        }
    }

    public HashMap<Integer, String> getNames() {
        return names;
    }

    public void setNames(HashMap<Integer, String> names) {
        this.names = names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return names.equals(author.names);
    }

    @Override
    public int hashCode() {
        return names.hashCode();
    }
}
