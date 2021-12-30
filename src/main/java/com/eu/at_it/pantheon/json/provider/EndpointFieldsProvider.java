package com.eu.at_it.pantheon.json.provider;

import com.eu.at_it.pantheon.json.annotations.Location;
import com.eu.at_it.pantheon.json.annotations.Protected;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.json.provider.functions.JsonString;
import com.eu.at_it.pantheon.json.provider.functions.ValueJsonValuePair;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EndpointFieldsProvider<T> {
    static final String NO_LOCATION_MSG = "Location identifier needs to be provided";
    static final String MULTIPLE_LOCATION_MSG = "There can be only one location identifier";
    private final Map<String, Field> accessibleFieldsMap = new HashMap<>();
    //todo JsonString function should be configurable so this provider could be generic
    private final Map<String, JsonString<T>> fieldToStringFunctionsMap = new HashMap<>();
    private final Map<String, FieldValueSetter<T>> fieldValueSettersMap = new HashMap<>();
    private final Class<? super T> tClass;
    private ValueJsonValuePair<T> locationGetter;

    @Inject
    public EndpointFieldsProvider(TypeLiteral<T> typeLiteral) {
        this.tClass = typeLiteral.getRawType();
    }

    public void init() {
        populateAccessibleFields(tClass);

        populateFieldToStringFunctionsMap();

        populateFieldValueSettersMap();

        populateLocationGetter();
    }

    public Map<String, JsonString<T>> fieldGetters() {
        return fieldToStringFunctionsMap;
    }

    public Map<String, FieldValueSetter<T>> fieldSetters() {
        return fieldValueSettersMap;
    }

    public ValueJsonValuePair<T> locationGetter() {
        return locationGetter;
    }

    Map<String, Field> accessibleFieldsMap() {
        return accessibleFieldsMap;
    }

    private JsonString<T> toStringFunction(Field field, String fieldName) {
        return new JsonString<>(field, fieldName);
    }

    private boolean allowed(Field field) {
        return field.getAnnotation(Protected.class) == null; //todo handle protection validation
    }

    void populateLocationGetter() {
        accessibleFieldsMap.forEach((fieldName, field) -> {
            if (field.getAnnotation(Location.class) != null) {
                if (locationGetter != null) {
                    throw new RuntimeException(MULTIPLE_LOCATION_MSG);
                }

                JsonString<T> fieldTStringBiFunction;
                if (fieldToStringFunctionsMap.containsKey(fieldName)) {
                    fieldTStringBiFunction = fieldToStringFunctionsMap.get(fieldName);
                } else {
                    fieldTStringBiFunction = toStringFunction(field, fieldName);
                }

                locationGetter = new ValueJsonValuePair<>(field, fieldTStringBiFunction);
            }
        });

        if (locationGetter == null) throw new RuntimeException(NO_LOCATION_MSG);
    }

    void populateFieldValueSettersMap() {
        accessibleFieldsMap.forEach((fieldName, field) -> {
            if (allowed(field)) {
                fieldValueSettersMap.put(fieldName, new FieldValueSetter<>(field));
            }
        });
    }

    void populateFieldToStringFunctionsMap() {
        accessibleFieldsMap.forEach((fieldName, field) -> {
            if (allowed(field)) {
                fieldToStringFunctionsMap.put(fieldName, toStringFunction(field, fieldName));
            }
        });
    }

    void populateAccessibleFields(Class<? super T> tClass) {
        for (Field declaredField : tClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            accessibleFieldsMap.put(declaredField.getName(), declaredField);
        }
    }
}
