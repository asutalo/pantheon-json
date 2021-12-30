package com.eu.at_it.json.provider.functions;

import java.lang.reflect.Field;
import java.util.function.Function;

public class JsonString<T> implements Function<T, String> {

    private final Field field;
    private final String fieldName;

    public JsonString(Field field, String fieldName) {
        this.field = field;
        this.fieldName = fieldName;
    }

    @Override
    public String apply(T t) {
        try {
            return String.format("\"%s\":\"%s\"", fieldName, field.get(t));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}