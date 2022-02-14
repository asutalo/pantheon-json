package com.eu.at_it.pantheon.json.endpoint;

import com.eu.at_it.pantheon.exceptions.PantheonProviderException;
import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.json.TestClass;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProviderCache;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.json.provider.functions.JsonString;
import com.eu.at_it.pantheon.json.provider.functions.ValueJsonValuePair;
import com.eu.at_it.pantheon.service.ServiceProviderRegistry;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericJsonEndpointTest {
    private static final String SOME_STRING = "someString";
    private static final String SOME_OTHER_STRING = "someOtherString";
    private final TestClass testClass = new TestClass();
    private final TypeLiteral<TestClass> testClassTypeLiteral = TypeLiteral.get(TestClass.class);
    private GenericJsonEndpoint<TestClass, Object> genericJsonEndpoint;

    @Mock
    private EndpointFieldsProvider<Object> mockEndpointFieldsProvider;

    @Mock
    private JsonString<Object> mockJsonString;

    @Mock
    private FieldValueSetter<Object> mockFieldValueSetter;

    @Mock
    private ValueJsonValuePair<Object> mockValueJsonValuePair;

    @Mock
    private Pair<String, String> mockPair;

    @Mock
    private ServiceProviderRegistry mockServiceProviderRegistry = mock(ServiceProviderRegistry.class);

    @Mock
    private EndpointFieldsProviderCache mockEndpointFieldsProviderCache;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field instance = ServiceProviderRegistry.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);

        instance.set(ServiceProviderRegistry.INSTANCE(), mockServiceProviderRegistry);
        EndpointFieldsProviderCache.setInstance(mockEndpointFieldsProviderCache);
        when(mockEndpointFieldsProviderCache.endpointFieldsProviderFor(any())).thenReturn(mockEndpointFieldsProvider);
        genericJsonEndpoint = new TestImpl("", testClassTypeLiteral);

        verify(mockEndpointFieldsProviderCache).endpointFieldsProviderFor(testClassTypeLiteral);
    }

    @Test
    void toString_shouldUseFieldValueGetters() {
        String expected = """
                {
                \tsomeString,
                \tsomeOtherString
                }""";

        when(mockEndpointFieldsProvider.fieldGetters()).thenReturn(Map.of(SOME_STRING, mockJsonString, SOME_OTHER_STRING, mockJsonString));
        when(mockJsonString.apply(testClass)).thenReturn(SOME_STRING).thenReturn(SOME_OTHER_STRING);

        String actualString = genericJsonEndpoint.toString(testClass);

        Assertions.assertEquals(expected, actualString);
    }

    @Test
    void setters_shouldReturnFieldValueSetters() {
        Map<String, FieldValueSetter<Object>> expectedFieldValueSetters = Map.of(SOME_STRING, mockFieldValueSetter, SOME_OTHER_STRING, mockFieldValueSetter);
        when(mockEndpointFieldsProvider.fieldSetters()).thenReturn(expectedFieldValueSetters);

        Assertions.assertEquals(expectedFieldValueSetters, genericJsonEndpoint.setters());
    }

    @Test
    void location_shouldReturnLocationDetails() {
        when(mockEndpointFieldsProvider.locationGetter()).thenReturn(mockValueJsonValuePair);
        when(mockValueJsonValuePair.apply(testClass)).thenReturn(mockPair);

        Assertions.assertEquals(mockPair, genericJsonEndpoint.getLocation(testClass));
    }

    @Test
    void shouldThrowExceptionWhenClassNotAnnotated() {
        PantheonProviderException pantheonProviderException = Assertions.assertThrows(PantheonProviderException.class, () -> new OtherTestImpl("", TypeLiteral.get(TestClassNotServed.class)));

        Assertions.assertEquals("@ServedBy annotation missing for type: class com.eu.at_it.pantheon.json.endpoint.GenericJsonEndpointTest$TestClassNotServed", pantheonProviderException.getMessage());
    }

    static class TestImpl extends GenericJsonEndpoint<TestClass, Object> {
        public TestImpl(String uriDefinition, TypeLiteral<TestClass> typeLiteral) {
            super(uriDefinition, typeLiteral);
        }
    }

    static class OtherTestImpl extends GenericJsonEndpoint<TestClassNotServed, Object> {
        public OtherTestImpl(String uriDefinition, TypeLiteral<TestClassNotServed> typeLiteral) {
            super(uriDefinition, typeLiteral);
        }
    }

    static class TestClassNotServed {
    }
}