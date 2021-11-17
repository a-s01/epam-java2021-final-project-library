package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.EditableEntity;

import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Booking extends EditableEntity {
    private static final long serialVersionUID = 1L;

    private User user;
    private State state;
    private Place located;
    private Set<Book> books;

    private Booking(long id, Date created, EditRecord lastEdit, User user, State state, Place located,
                    Set<Book> books) {
        super(id, created, lastEdit);
        this.user = user;
        this.state = state;
        this.located = located;
        this.books = books;
    }

    public static class Builder extends EditableEntity.Builder {
        private User user;
        private State state;
        private Place located;
        private Set<Book> books = new HashSet<>();

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setState(State state) {
            this.state = state;
            return this;
        }

        public Builder setLocated(Place located) {
            this.located = located;
            return this;
        }

        public Builder setBooks(Set<Book> books) {
            this.books = books;
            return this;
        }
        
        public Builder addBook(Book book) {
            books.add(book);
            return this;
        }
        
        public Booking build() {
            if (state == null) {
                state = State.NEW;
            }
            if (located == null) {
                located = Place.LIBRARY;
            }
            
            return new Booking(id, created, lastEdit, user, state, located, books);
        }
    }
    public enum State {
        UNKNOWN, NEW, BOOKED, DELIVERED, DONE, CANCELED
    }

    public enum Place {
        UNKNOWN, LIBRARY, USER
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

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return user.equals(booking.user) && books.equals(booking.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, books);
    }
}
