package com.eu.at_it.pantheon.json.provider.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class FieldValueSetterTest {
    @Test
    void accept_shouldUpdateFieldValue() throws IllegalAccessException {
        int expectedValue = 2;
        TestClass testClass = new TestClass();
        Field declaredField = testClass.getClass().getDeclaredFields()[1];
        int startingValue = (int) declaredField.get(testClass);
        FieldValueSetter<TestClass> objectFieldValueSetter = new FieldValueSetter<>(declaredField);
        objectFieldValueSetter.accept(testClass, expectedValue);

        int finalValue = (int) declaredField.get(testClass);

        Assertions.assertNotEquals(startingValue, finalValue);
        Assertions.assertEquals(expectedValue, finalValue);
    }

    @Test
    void accept_shouldThrowExceptionIfFieldCannotBeSet() {
        TestClass testClass = new TestClass();
        Field declaredField = testClass.getClass().getDeclaredFields()[0];

        FieldValueSetter<TestClass> objectFieldValueSetter = new FieldValueSetter<>(declaredField);

        Assertions.assertThrows(RuntimeException.class, () -> objectFieldValueSetter.accept(testClass, 6));
    }
}