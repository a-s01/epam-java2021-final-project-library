package com.epam.java2021.library.entity;

import java.sql.Date;

public class ModifiableEntity extends Entity {
    protected Date modified;

    protected ModifiableEntity(long id, Date modified) {
        super(id);
        this.modified = modified;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
