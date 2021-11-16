package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.service.DependencyHolder;

@FunctionalInterface
public interface ComplexType<E extends Entity> {
    DependencyHolder<E> getDependencies();
}
