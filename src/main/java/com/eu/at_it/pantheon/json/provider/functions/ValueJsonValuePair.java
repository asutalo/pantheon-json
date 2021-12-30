package com.eu.at_it.pantheon.json.provider.functions;

import com.eu.at_it.pantheon.helper.Pair;

import java.lang.reflect.Field;
import java.util.function.Function;

public class ValueJsonValuePair<T> implements Function<T, Pair<String, String>> {
    private final Field field;
    private final JsonString<T> jsonString;

    public ValueJsonValuePair(Field field, JsonString<T> jsonString) {
        this.field = field;
        this.jsonString = jsonString;
    }

    @Override
    public Pair<String, String> apply(T t) {
        try {
            return new Pair<>(String.valueOf(field.get(t)), jsonString.apply(t));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getField() {
        return field;
    }
}