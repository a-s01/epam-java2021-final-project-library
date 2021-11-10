package com.epam.java2021.library.entity.entityImpl;

import com.epam.java2021.library.entity.EditableEntity;

import java.sql.Date;

public class User extends EditableEntity {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String salt;
    private Role role;
    private State state;
    private double fine;
    private String name;

    public enum Role {
        UNKNOWN, USER, LIBRARIAN, ADMIN
    }
    public enum State {
        UNKNOWN, VALID, BLOCKED, DELETED
    }

    /**
     * This class also sets defaults for id, role and state. If they are not defined, default values will be applied.
     */
    public static class Builder extends EditableEntity.Builder {
        private String email;
        private String password;
        private String salt;
        private Role role;
        private State state;
        private double fine;
        private String name;

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setSalt(String salt) {
            this.salt = salt;
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setState(State state) {
            this.state = state;
            return this;
        }

        public Builder setFine(double fine) {
            this.fine = fine;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public User build() {
            // set default values
            if (role == null) {
                role = Role.USER;
            }

            if (state == null) {
                state = State.VALID;
            }
            return new User(id, created, lastEdit, email, password, salt, role, state, fine, name);
        }
    }

    public User(long id, Date created, EditRecord lastEdit, String email,
                String password, String salt, Role role, State state, double fine, String name) {
        super(id, created, lastEdit);
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.role = role;
        this.state = state;
        this.fine = fine;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
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
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "lastEdit=" + lastEdit +
                ", id=" + id +
                ", created=" + created +
                ", email='" + email + '\'' +
                ", password='hidden'" +
                ", salt='hidden'" +
                ", role=" + role +
                ", state=" + state +
                ", fine=" + fine +
                ", name='" + name + '\'' +
                '}';
    }
}