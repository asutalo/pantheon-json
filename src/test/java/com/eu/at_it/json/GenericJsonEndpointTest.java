package com.eu.at_it.json;

import com.eu.at_it.json.provider.EndpointFieldsProvider;
import com.eu.at_it.json.provider.functions.FieldValueSetter;
import com.eu.at_it.json.provider.functions.JsonString;
import com.eu.at_it.json.provider.functions.ValueJsonValuePair;
import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.service.data.DataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericJsonEndpointTest {
    static final int SOME_INT = 1;
    static final String VAR = "string";
    static final String OTHER_VAR = "integer";
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

    private GenericJsonEndpoint<Object, Object> genericJsonEndpoint;

    @BeforeEach
    void setUp() {
        genericJsonEndpoint = new TestImpl("", mockDataAccessService, mockEndpointFieldsProvider);
    }

    @Test
    void shouldInitialiseEndpointFieldsProvider() {
        verify(mockEndpointFieldsProvider).init();
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
        public TestImpl(String uriDefinition, DataService<Object, Object> service, EndpointFieldsProvider<Object> endpointFieldsProvider) {
            super(uriDefinition, service, endpointFieldsProvider);
        }
    }
}