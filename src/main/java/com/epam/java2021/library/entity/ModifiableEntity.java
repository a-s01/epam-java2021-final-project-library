package com.epam.java2021.library.entity;

import java.util.Calendar;

public class ModifiableEntity extends Entity {
    protected Calendar modified;

    protected ModifiableEntity(long id, Calendar modified) {
        super(id);
        this.modified = modified;
    }

    public Calendar getModified() {
        return modified;
    }

    public void setModified(Calendar modified) {
        this.modified = modified;
    }
}
