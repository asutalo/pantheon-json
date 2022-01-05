package com.eu.at_it.pantheon.json.endpoint;

import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.json.TestClass;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProviderCache;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.json.provider.functions.JsonString;
import com.eu.at_it.pantheon.json.provider.functions.ValueJsonValuePair;
import com.eu.at_it.pantheon.service.Service;
import com.eu.at_it.pantheon.service.ServiceProvider;
import com.eu.at_it.pantheon.service.ServiceProviderRegistry;
import com.eu.at_it.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericJsonEndpointTest {
    private static final String SOME_STRING = "someString";
    private static final String SOME_OTHER_STRING = "someOtherString";

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

    private final TypeLiteral<TestClass> mockTypeLiteral = TypeLiteral.get(TestClass.class);
    @Mock
    private TestClass mockObject;

    @Mock
    private EndpointFieldsProviderCache mockEndpointFieldsProviderCache;
    private GenericJsonEndpoint<TestClass, Object> genericJsonEndpoint;

    @BeforeAll
    static void initSetUp() {
        DataService mockDataService = mock(DataService.class);

        ServiceProviderRegistry.INSTANCE().register(new ServiceProvider() {
            @Override
            public Service provide(TypeLiteral<?> servingType) {
                return mockDataService;
            }

            @Override
            public TypeLiteral<? extends Service> providerFor() {
                return TypeLiteral.get(DataService.class);
            }
        });
    }

    @BeforeEach
    void setUp() {
        EndpointFieldsProviderCache.setInstance(mockEndpointFieldsProviderCache);
        when(mockEndpointFieldsProviderCache.endpointFieldsProviderFor(any())).thenReturn(mockEndpointFieldsProvider);
        genericJsonEndpoint = new TestImpl("", mockTypeLiteral);

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

    static class TestImpl extends GenericJsonEndpoint<TestClass, Object> {
        public TestImpl(String uriDefinition, TypeLiteral<TestClass> typeLiteral) {
            super(uriDefinition, typeLiteral);
        }
    }
}