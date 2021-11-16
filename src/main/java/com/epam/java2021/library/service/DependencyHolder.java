package com.epam.java2021.library.service;

import com.epam.java2021.library.entity.Entity;

import java.util.HashMap;

public class DependencyHolder<E extends Entity> {
    private final HashMap<E, HashMap<String, Long>> map = new HashMap<>();

    public void set(E entity, String key, long id) {
        if (map.containsKey(entity)) {
            map.get(entity).put(key, id);
            return;
        }
        HashMap<String, Long> values = new HashMap<>();
        values.put(key, id);
        map.put(entity, values);
    }

    public void clear() {
        map.clear();
    }

    public HashMap<String, Long> get(E entity) {
        return map.get(entity);
    }
}
