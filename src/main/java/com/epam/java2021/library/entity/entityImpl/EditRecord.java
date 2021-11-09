package com.epam.java2021.library.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EditRecord extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private long editBy;
    private String description;
    private String remark;

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
