package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.ModifiableEntity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Booking extends ModifiableEntity {
    private static final long serialVersionUID = 1L;

    private User user;
    private State state;
    private Place located;
    private List<Book> books;

    private Booking(long id, Date modified, User user, State state, Place located,
                    List<Book> books) {
        super(id, modified);
        this.user = user;
        this.state = state;
        this.located = located;
        this.books = books;
    }

    public static class Builder {
        private long id;
        private Date modified;
        private User user;
        private State state;
        private Place located;
        private List<Book> books = new ArrayList<>();

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setModified(Date modified) {
            this.modified = modified;
            return this;
        }

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

        public Builder setBooks(List<Book> books) {
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
            
            return new Booking(id, modified, user, state, located, books);
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
        return user.equals(booking.user) && books.equals(booking.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, books);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "created=" + modified +
                ", id=" + id +
                ", user=" + user +
                ", state=" + state +
                ", located=" + located +
                ", books=" + books +
                '}';
    }
}
