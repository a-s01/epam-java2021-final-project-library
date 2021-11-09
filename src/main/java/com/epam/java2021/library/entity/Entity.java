package com.epam.java2021.library.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Entity implements Serializable {
    protected long id;
    protected LocalDateTime created;

    protected Entity(long id, LocalDateTime created) {
        this.id = id;
        this.created = created;
    }

    protected static class Builder {
        protected long id = -1;
        protected LocalDateTime created;

        public void setId(long id) {
            this.id = id;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
