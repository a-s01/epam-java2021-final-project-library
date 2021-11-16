package com.epam.java2021.library.entity;

import com.epam.java2021.library.entity.entityImpl.EditRecord;

import java.sql.Date;

public abstract class EditableEntity extends CreatableEntity {
    protected EditRecord lastEdit;

    protected EditableEntity(long id, Date created, EditRecord lastEdit) {
        super(id, created);
        this.lastEdit = lastEdit;
    }

    protected static class Builder extends CreatableEntity.Builder {
        protected EditRecord lastEdit;

        public void setLastEdit(EditRecord lastEdit) {
            this.lastEdit = lastEdit;
        }
    }

    public EditRecord getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(EditRecord lastEdit) {
        this.lastEdit = lastEdit;
    }
}
