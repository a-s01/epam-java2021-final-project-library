package com.epam.java2021.library.entity;

import java.io.Serializable;
import java.sql.Date;

public abstract class Entity implements Serializable {
    protected long id;

    protected Entity(long id) {
        this.id = id;
    }

    protected static class Builder {
        protected long id = -1;

        public void setId(long id) {
            this.id = id;
        }

    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
