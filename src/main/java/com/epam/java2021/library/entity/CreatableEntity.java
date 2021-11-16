package com.epam.java2021.library.entity;

import java.sql.Date;

public class CreatableEntity extends Entity {
    protected Date created;

    protected CreatableEntity(long id, Date created) {
        super(id);
        this.created = created;
    }

    protected static class Builder extends Entity.Builder {
        protected Date created;

        public void setCreated(Date created) {
            this.created = created;
        }
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
