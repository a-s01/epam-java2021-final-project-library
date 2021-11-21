package com.epam.java2021.library.service.util;

@FunctionalInterface
public interface TypeConverter<K, V> {
    V process(K k);
}
