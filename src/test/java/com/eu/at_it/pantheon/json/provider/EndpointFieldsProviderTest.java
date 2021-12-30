package com.eu.at_it.pantheon.json.provider;

import com.eu.at_it.pantheon.json.annotations.Location;
import com.eu.at_it.pantheon.json.annotations.Protected;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.json.provider.functions.JsonString;
import com.eu.at_it.pantheon.json.provider.functions.ValueJsonValuePair;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Map;

import static com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider.MULTIPLE_LOCATION_MSG;
import static com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider.NO_LOCATION_MSG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class EndpointFieldsProviderTest {
    static final String ID_VAR = "id";
    static final String PROTECTED_VAR = "protectedString";
    static final String NOT_PROTECTED_VAR = "notProtected";
    private EndpointFieldsProvider<TestClass> endpointFieldsProvider;

    @BeforeEach
    void setUp() {
        endpointFieldsProvider = new EndpointFieldsProvider<>(TypeLiteral.get(TestClass.class));
    }

    @Test
    void init() {
        EndpointFieldsProvider<TestClass> spy = Mockito.spy(endpointFieldsProvider);

        doNothing().when(spy).populateAccessibleFields(any());
        doNothing().when(spy).populateFieldToStringFunctionsMap();
        doNothing().when(spy).populateFieldValueSettersMap();
        doNothing().when(spy).populateLocationGetter();

        spy.init();

        verify(spy).populateAccessibleFields(TestClass.class);
        verify(spy).populateFieldToStringFunctionsMap();
        verify(spy).populateFieldValueSettersMap();
        verify(spy).populateLocationGetter();
    }

    @Test
    void populateLocationGetter_shouldThrowExceptionWhenLocationNotProvided() {
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> {
            EndpointFieldsProvider<NoLocationsTestClass> noLocationsTestClassEndpointFieldsProvider = new EndpointFieldsProvider<>(TypeLiteral.get(NoLocationsTestClass.class));
            noLocationsTestClassEndpointFieldsProvider.populateAccessibleFields(NoLocationsTestClass.class);
            noLocationsTestClassEndpointFieldsProvider.populateLocationGetter();
        });
        Assertions.assertEquals(NO_LOCATION_MSG, runtimeException.getMessage());
    }


    @Test
    void populateLocationGetter_shouldThrowExceptionWhenMultipleLocationProvided() {
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> {
            EndpointFieldsProvider<MultipleLocationsTestClass> multipleLocationsTestClassEndpointFieldsProvider = new EndpointFieldsProvider<>(TypeLiteral.get(MultipleLocationsTestClass.class));
            multipleLocationsTestClassEndpointFieldsProvider.populateAccessibleFields(MultipleLocationsTestClass.class);
            multipleLocationsTestClassEndpointFieldsProvider.populateLocationGetter();
        });
        Assertions.assertEquals(MULTIPLE_LOCATION_MSG, runtimeException.getMessage());
    }

    @Test
    void populateAccessibleFields() {
        Map<String, Field> accessibleFieldsMap = endpointFieldsProvider.accessibleFieldsMap();
        Assertions.assertTrue(accessibleFieldsMap.isEmpty());

        endpointFieldsProvider.populateAccessibleFields(TestClass.class);

        Assertions.assertFalse(accessibleFieldsMap.isEmpty());
        Assertions.assertTrue(accessibleFieldsMap.containsKey(ID_VAR));
        Assertions.assertTrue(accessibleFieldsMap.containsKey(PROTECTED_VAR));
        Assertions.assertTrue(accessibleFieldsMap.containsKey(NOT_PROTECTED_VAR));
    }

    @Test
    void populateFieldToStringFunctionsMap() {
        Map<String, JsonString<TestClass>> fieldToStringFunctionsMap = endpointFieldsProvider.fieldGetters();
        Assertions.assertTrue(fieldToStringFunctionsMap.isEmpty());

        endpointFieldsProvider.populateAccessibleFields(TestClass.class);
        endpointFieldsProvider.populateFieldToStringFunctionsMap();

        Assertions.assertFalse(fieldToStringFunctionsMap.isEmpty());
        Assertions.assertFalse(fieldToStringFunctionsMap.containsKey(PROTECTED_VAR));
        Assertions.assertTrue(fieldToStringFunctionsMap.containsKey(NOT_PROTECTED_VAR));
        Assertions.assertTrue(fieldToStringFunctionsMap.containsKey(ID_VAR));
    }

    @Test
    void populateFieldValueSettersMap() {
        Map<String, FieldValueSetter<TestClass>> fieldValueSettersMap = endpointFieldsProvider.fieldSetters();
        Assertions.assertTrue(fieldValueSettersMap.isEmpty());

        endpointFieldsProvider.populateAccessibleFields(TestClass.class);
        endpointFieldsProvider.populateFieldValueSettersMap();

        Assertions.assertFalse(fieldValueSettersMap.isEmpty());
        Assertions.assertFalse(fieldValueSettersMap.containsKey(PROTECTED_VAR));
        Assertions.assertTrue(fieldValueSettersMap.containsKey(NOT_PROTECTED_VAR));
        Assertions.assertTrue(fieldValueSettersMap.containsKey(ID_VAR));
    }

    @Test
    void populateLocationGetter() {
        Assertions.assertNull(endpointFieldsProvider.locationGetter());

        endpointFieldsProvider.populateAccessibleFields(TestClass.class);
        endpointFieldsProvider.populateLocationGetter();

        ValueJsonValuePair<TestClass> actual = endpointFieldsProvider.locationGetter();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ID_VAR, actual.getField().getName());
    }

    @Test
    void populateLocationGetter_shouldReuseFunctionFrom_populateFieldToStringFunctionsMap() {
        Assertions.assertNull(endpointFieldsProvider.locationGetter());

        endpointFieldsProvider.populateAccessibleFields(TestClass.class);
        endpointFieldsProvider.populateFieldToStringFunctionsMap();
        endpointFieldsProvider.populateLocationGetter();

        ValueJsonValuePair<TestClass> actual = endpointFieldsProvider.locationGetter();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ID_VAR, actual.getField().getName());
    }

    private static class TestClass {
        @Location
        private String id;

        @Protected
        private String protectedString;

        private int notProtected;
    }

    private static class MultipleLocationsTestClass {
        @Location
        private String id;

        @Location
        private String id2;
    }

    private static class NoLocationsTestClass {
    }
}