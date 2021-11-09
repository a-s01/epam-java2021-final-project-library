package com.epam.java2021.library.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Objects;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String title;
    private String isbn;
    private Year year;
    private long totalAmount;
    private long availableAmount;
    private long wasBookedTimes;
    private int keepPeriod; // int should be enough for keeping period, it will not be too long
    private LocalDateTime created;
    private EditRecord lastEdit;
    private List<Author> authors;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(long availableAmount) {
        this.availableAmount = availableAmount;
    }

    public long getWasBookedTimes() {
        return wasBookedTimes;
    }

    public void setWasBookedTimes(long wasBookedTimes) {
        this.wasBookedTimes = wasBookedTimes;
    }

    public int getKeepPeriod() {
        return keepPeriod;
    }

    public void setKeepPeriod(int keepPeriod) {
        this.keepPeriod = keepPeriod;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public EditRecord getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(EditRecord lastEdit) {
        this.lastEdit = lastEdit;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return title.equals(book.title) && isbn.equals(book.isbn) && year.equals(book.year)
                && authors.equals(book.authors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, isbn, year, authors);
    }
}
