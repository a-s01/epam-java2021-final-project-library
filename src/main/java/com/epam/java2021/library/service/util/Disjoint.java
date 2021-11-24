package com.epam.java2021.library.service.util;

import java.util.ArrayList;
import java.util.List;

public class Disjoint<T> {
    private List<T> toDelete;
    private List<T> toAdd;

    public Disjoint(List<T> old, List<T> newList) {
        toDelete = new ArrayList<>(old);
        toAdd = new ArrayList<>(newList);

        toAdd.removeAll(toDelete);
        toDelete.removeAll(newList);
    }

    public List<T> getToDelete() {
        return toDelete;
    }

    public List<T> getToAdd() {
        return toAdd;
    }
}