package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.ModifiableEntity;

import java.util.*;
import java.util.stream.Collectors;

public class Author extends ModifiableEntity {
    private static final long serialVersionUID = 1L;

    private String name;
    private Map<Lang, String> i18Names;

    private Author(long id, Calendar created, String name, Map<Lang, String> i18Names) {
        super(id, created);
        this.name = name;
        this.i18Names = i18Names;
    }

    public static class Builder {
        private long id = -1;
        private Calendar modified;
        private String name;
        private Map<Lang, String> i18Names;

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
            this.i18Names = convertToMap(i18Names);
        }

        public Author build() {
            return new Author(id, modified, name, i18Names);
        }
    }

    private static Map<Lang, String> convertToMap(List<I18AuthorName> i18Names) {
        Map<Lang, String> map = new HashMap<>();
        for (I18AuthorName name: i18Names) {
            map.put(name.getLang(), name.getName());
        }
        return map;
    }

    public List<I18AuthorName> getI18NamesAsList() {
        return i18Names.entrySet()
                .stream()
                .map(x -> new I18AuthorName.Builder().setLang(x.getKey()).setName(x.getValue()).build())
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(Lang lang) {
        return getName(lang, true);
    }

    public String getName(Lang lang, boolean fallback) {
        String s = i18Names.get(lang);

        if (s == null && fallback) {
            return name;
        }
        return s == null ? "" : s;
    }

    public void setName(Lang lang, String s) {
        String oldName = i18Names.get(lang);

        if (oldName != null) {
            if (oldName.equals(name)) {
                name = s;
            }
            i18Names.replace(lang, oldName, s);
            return;
        }

        i18Names.put(lang, s);
    }

    public Map<Lang, String> getI18Names() {
        return i18Names;
    }

    public void setI18Names(Map<Lang, String> i18Names) {
        this.i18Names = i18Names;
    }

    public void setI18Names(List<I18AuthorName> i18Names) {
        this.i18Names = convertToMap(i18Names);
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
