package com.eu.at_it.service.data;

import java.util.List;
import java.util.Map;

public interface DataService<T, Q> {
    T update(T toUpdate) throws Exception;

    T save(T toSave) throws Exception;

    void delete(T toDelete) throws Exception;

    Q filteredSelect();

    T get(Q filteredSelect) throws Exception;

    T get(Map<String, Object> filter) throws Exception;

    List<T> getAll() throws Exception;

    List<T> getAll(Map<String, Object> filter) throws Exception;

    List<T> getAll(Q filteredSelect) throws Exception;

    T instanceOfT(Map<String, Object> values);
}
