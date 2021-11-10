package com.epam.java2021.library.entity;

import java.io.Serializable;
import java.sql.Date;

public abstract class Entity implements Serializable {
    protected long id;
    protected Date created;

    protected Entity(long id, Date created) {
        this.id = id;
        this.created = created;
    }

    protected static class Builder {
        protected long id = -1;
        protected Date created;

        public void setId(long id) {
            this.id = id;
        }

        public void setCreated(Date created) {
            this.created = created;
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
