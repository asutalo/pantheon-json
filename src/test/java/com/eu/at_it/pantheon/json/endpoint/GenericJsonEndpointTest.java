package com.eu.at_it.pantheon.json.endpoint;

import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProviderCache;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.json.provider.functions.JsonString;
import com.eu.at_it.pantheon.json.provider.functions.ValueJsonValuePair;
import com.eu.at_it.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericJsonEndpointTest {
    private static final String SOME_STRING = "someString";
    private static final String SOME_OTHER_STRING = "someOtherString";
    @Mock
    private DataService<Object, Object> mockDataAccessService;

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
    private Object mockObject;

    @Mock
    private TypeLiteral<Object> mockTypeLiteral;

    @Mock
    private EndpointFieldsProviderCache mockEndpointFieldsProviderCache;

    private GenericJsonEndpoint<Object, Object> genericJsonEndpoint;

    @BeforeEach
    void setUp() {
        EndpointFieldsProviderCache.setInstance(mockEndpointFieldsProviderCache);
        when(mockEndpointFieldsProviderCache.endpointFieldsProviderFor(any())).thenReturn(mockEndpointFieldsProvider);
        genericJsonEndpoint = new TestImpl("", mockDataAccessService, mockTypeLiteral);

        verify(mockEndpointFieldsProviderCache).endpointFieldsProviderFor(mockTypeLiteral);
    }

    @Test
    void toString_shouldUseFieldValueGetters() {
        String expected = """
                {
                \tsomeString,
                \tsomeOtherString
                }""";

        when(mockEndpointFieldsProvider.fieldGetters()).thenReturn(Map.of(SOME_STRING, mockJsonString, SOME_OTHER_STRING, mockJsonString));
        when(mockJsonString.apply(mockObject)).thenReturn(SOME_STRING).thenReturn(SOME_OTHER_STRING);

        String actualString = genericJsonEndpoint.toString(mockObject);

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
        when(mockValueJsonValuePair.apply(mockObject)).thenReturn(mockPair);

        Assertions.assertEquals(mockPair, genericJsonEndpoint.getLocation(mockObject));
    }

    static class TestImpl extends GenericJsonEndpoint<Object, Object> {
        public TestImpl(String uriDefinition, DataService<Object, Object> service, TypeLiteral<Object> typeLiteral) {
            super(uriDefinition, service, typeLiteral);
        }
    }
}