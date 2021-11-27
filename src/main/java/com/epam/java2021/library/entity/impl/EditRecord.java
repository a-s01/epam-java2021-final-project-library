package com.epam.java2021.library.entity.impl;

import com.epam.java2021.library.entity.ModifiableEntity;

import java.util.Calendar;

public class EditRecord extends ModifiableEntity {
    private static final long serialVersionUID = 1L;

    private long editBy;
    private String description;
    private String remark;

    public EditRecord(long id, Calendar modified, long editBy, String description, String remark) {
        super(id, modified);
        this.editBy = editBy;
        this.description = description;
        this.remark = remark;
    }

    public static class Builder {
        private long id;
        private Calendar modified;
        private long editBy;
        private String description;
        private String remark;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setModified(Calendar modified) {
            this.modified = modified;
            return this;
        }

        public Builder setEditBy(long editBy) {
            this.editBy = editBy;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public EditRecord build() {
            return new EditRecord(id, modified, editBy, description, remark);
        }
    }
    public long getEditBy() {
        return editBy;
    }

    public void setEditBy(long editBy) {
        this.editBy = editBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "EditRecord{" +
                "id=" + id +
                ", modified=" + modified +
                ", editBy=" + editBy +
                ", description='" + description + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}