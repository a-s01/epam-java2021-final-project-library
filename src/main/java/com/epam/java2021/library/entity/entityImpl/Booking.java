package com.epam.java2021.library.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private User user;
    private State state;
    private Place located;
    private LocalDateTime created;
    private EditRecord lastEdit;
    private List<Book> books;

    public enum State {
        UNKNOWN, NEW, BOOKED, DELIVERED, DONE, CANCELED
    }

    public enum Place {
        UNKNOWN, LIBRARY, USER
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Place getLocated() {
        return located;
    }

    public void setLocated(Place located) {
        this.located = located;
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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return user.equals(booking.user) && created.equals(booking.created) && books.equals(booking.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, created, books);
    }
}
