package com.eu.at_it.pantheon.json.provider.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class JsonStringTest {
    @Test
    void apply_shouldReturnFieldAsJsonString() {
        TestClass testClass = new TestClass();
        Field declaredField = testClass.getClass().getDeclaredFields()[1];

        String actual = new JsonString<>(declaredField, declaredField.getName()).apply(testClass);

        Assertions.assertEquals("\"someInt\":\"1\"", actual);
    }

    @Test
    void apply_shouldThrowExceptionIfFieldNotReadable() {
        TestClass testClass = new TestClass();
        Field declaredField = testClass.getClass().getDeclaredFields()[0];

        Assertions.assertThrows(RuntimeException.class, () -> new JsonString<>(declaredField, declaredField.getName()).apply(testClass));
    }
}