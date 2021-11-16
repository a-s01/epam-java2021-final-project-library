package com.epam.java2021.library.entity.entityImpl;

import com.epam.java2021.library.entity.EditableEntity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Book extends EditableEntity {
    private static final long serialVersionUID = 1L;

    private String title;
    private String isbn;
    private int year;
    private Language lang;
    private BookStat bookStat;
    private int keepPeriod; // int should be enough for keeping period, it will not be too long
    private List<Author> authors;

    private Book(long id, Date created, EditRecord lastEdit, String title, String isbn, int year,
                 Language lang, BookStat bookStat, int keepPeriod, List<Author> authors) {
        super(id, created, lastEdit);
        this.title = title;
        this.isbn = isbn;
        this.year = year;
        this.lang = lang;
        this.bookStat = bookStat;
        this.keepPeriod = keepPeriod;
        this.authors = authors;
    }
    
    public static class Builder extends EditableEntity.Builder {
        private String title;
        private String isbn;
        private int year;
        private Language lang;
        private BookStat bookStat;
        private int keepPeriod;
        private List<Author> authors = new ArrayList<>();
        
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setIsbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder setLang(Language lang) {
            this.lang = lang;
            return this;
        }

        public Builder setBookStat(BookStat bookStat) {
            this.bookStat = bookStat;
            return this;
        }

        public Builder setKeepPeriod(int keepPeriod) {
            this.keepPeriod = keepPeriod;
            return this;
        }

        public Builder setAuthors(List<Author> authors) {
            this.authors = authors;
            return this;
        }
        
        public Builder addAuthor(Author author) {
            authors.add(author);
            return this;
        }

        public Builder setYear(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            this.year = cal.get(Calendar.YEAR);
            return this;
        }

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public Book build() {
            return new Book(id, created, lastEdit, title, isbn, year, lang, bookStat, keepPeriod, authors);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BookStat getBookStat() {
        return bookStat;
    }

    public void setBookStat(BookStat bookStat) {
        this.bookStat = bookStat;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public int getKeepPeriod() {
        return keepPeriod;
    }

    public void setKeepPeriod(int keepPeriod) {
        this.keepPeriod = keepPeriod;
    }

    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return title.equals(book.title) && isbn.equals(book.isbn) && year == book.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, isbn, year);
    }

    @Override
    public String toString() {
        return "Book{" +
                "lastEdit=" + lastEdit +
                ", id=" + id +
                ", created=" + created +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", year=" + year +
                ", lang=" + lang +
                ", bookStat=" + bookStat +
                ", keepPeriod=" + keepPeriod +
                ", authors=" + authors +
                '}';
    }
}
