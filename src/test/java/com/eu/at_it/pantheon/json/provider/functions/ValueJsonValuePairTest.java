package com.eu.at_it.pantheon.json.provider.functions;

import com.eu.at_it.pantheon.helper.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValueJsonValuePairTest {
    @Mock
    private JsonString<TestClass> mockJsonStringFunction;

    @Test
    void apply_shouldReturnPairWithValueAndValueAsJson() {
        Field declaredField = TestClass.class.getDeclaredFields()[1];
        TestClass testClass = new TestClass();

        String someString = "someString";

        when(mockJsonStringFunction.apply(testClass)).thenReturn(someString);
        Pair<String, String> actual = new ValueJsonValuePair<>(declaredField, mockJsonStringFunction).apply(testClass);

        Assertions.assertEquals("1", actual.left());
        Assertions.assertEquals(someString, actual.right());
    }

    @Test
    void apply_shouldThrowExceptionWhenFieldNotReadable() {
        Field declaredField = TestClass.class.getDeclaredFields()[0];
        TestClass testClass = new TestClass();

        Assertions.assertThrows(RuntimeException.class, () -> new ValueJsonValuePair<>(declaredField, mockJsonStringFunction).apply(testClass));
    }
}