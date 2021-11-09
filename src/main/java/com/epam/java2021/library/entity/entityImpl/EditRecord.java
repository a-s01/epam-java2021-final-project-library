package com.epam.java2021.library.entity.entityImpl;

import com.epam.java2021.library.entity.Entity;

import java.time.LocalDateTime;

public class EditRecord extends Entity {
    private static final long serialVersionUID = 1L;

    private long editBy;
    private String description;
    private String remark;

    public EditRecord(long id, LocalDateTime created, long editBy, String description, String remark) {
        super(id, created);
        this.editBy = editBy;
        this.description = description;
        this.remark = remark;
    }

    public static class Builder extends Entity.Builder {
        private long editBy;
        private String description;
        private String remark;

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
            return new EditRecord(id, created, editBy, description, remark);
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
}