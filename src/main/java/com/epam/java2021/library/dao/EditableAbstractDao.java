package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.EditableEntity;
import com.epam.java2021.library.entity.impl.EditRecord;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

public interface EditableAbstractDao<E extends EditableEntity> extends AbstractDao<E> {
    void setLastEdit(E editableEntity, EditRecord lastEdit) throws DaoException, ServiceException;
}
