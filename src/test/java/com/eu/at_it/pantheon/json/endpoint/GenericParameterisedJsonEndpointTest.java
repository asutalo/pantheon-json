package com.eu.at_it.pantheon.json.endpoint;

import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.json.TestClass;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProviderCache;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.server.response.Response;
import com.eu.at_it.pantheon.server.response.exception.InternalServerErrorException;
import com.eu.at_it.pantheon.server.response.exception.UnprocessableEntityException;
import com.eu.at_it.pantheon.service.ServiceProviderRegistry;
import com.eu.at_it.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.eu.at_it.pantheon.json.endpoint.GenericJsonEndpoint.ACCEPTED;
import static com.eu.at_it.pantheon.json.endpoint.GenericJsonEndpoint.OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericParameterisedJsonEndpointTest {
    private static final Map<String, Object> EMPTY_MAP = Map.of();
    private static final Map<String, Object> SOME_REQUEST_BODY = Map.of("1", 1);
    private static final String SOME_STRING = "someString";
    private static final String SOME_OTHER_STRING = "someOtherString";

    @Mock
    private DataService<Object, Object> mockDataAccessService;

    @Mock
    private FieldValueSetter<Object> mockFieldValueSetter;

    @Mock
    private EndpointFieldsProvider<Object> mockEndpointFieldsProvider;

    @Mock
    private EndpointFieldsProviderCache mockEndpointFieldsProviderCache;

    private final TestClass testClass = new TestClass();
    private final TypeLiteral<TestClass> testClassTypeLiteral = TypeLiteral.get(TestClass.class);
    private GenericParameterisedJsonEndpoint<TestClass, Object> genericParameterisedJsonEndpoint;
    @Mock
    private ServiceProviderRegistry mockServiceProviderRegistry = mock(ServiceProviderRegistry.class);

    @BeforeEach
    void setUp() {
        ServiceProviderRegistry.setInstance(mockServiceProviderRegistry);
        EndpointFieldsProviderCache.setInstance(mockEndpointFieldsProviderCache);
        when(mockEndpointFieldsProviderCache.endpointFieldsProviderFor(any())).thenReturn(mockEndpointFieldsProvider);
        when(mockServiceProviderRegistry.getService(any(), any())).thenReturn(mockDataAccessService);
        genericParameterisedJsonEndpoint = spy(new GenericParameterisedJsonEndpoint("", testClassTypeLiteral));

        verify(mockEndpointFieldsProviderCache).endpointFieldsProviderFor(testClassTypeLiteral);
    }

    @Test
    void get() throws Exception {
        doReturn(SOME_STRING).when(genericParameterisedJsonEndpoint).toString(any());
        when(mockDataAccessService.get(EMPTY_MAP)).thenReturn(testClass);

        Response response = genericParameterisedJsonEndpoint.get(EMPTY_MAP, SOME_REQUEST_BODY, mock(Headers.class));

        Assertions.assertEquals(OK, response.getStatusCode());
        Assertions.assertEquals(SOME_STRING, response.getMessage());
    }

    @Test
    void delete() throws Exception {
        Pair<String, String> locationPair = new Pair<>(SOME_OTHER_STRING, SOME_STRING);

        when(mockDataAccessService.get(EMPTY_MAP)).thenReturn(testClass);
        doReturn(SOME_STRING).when(genericParameterisedJsonEndpoint).withBracers(SOME_STRING);
        doReturn(locationPair).when(genericParameterisedJsonEndpoint).getLocation(testClass);

        Response response = genericParameterisedJsonEndpoint.delete(EMPTY_MAP, SOME_REQUEST_BODY, mock(Headers.class));

        verify(mockDataAccessService).delete(testClass);
        Assertions.assertEquals(ACCEPTED, response.getStatusCode());
        Assertions.assertEquals(SOME_STRING, response.getMessage());
    }

    @Test
    void put() throws Exception {
        String updatedVal = "new val";
        String otherUpdatedVal = "other new val";

        when(mockDataAccessService.get(EMPTY_MAP)).thenReturn(testClass);
        doReturn(Map.of(SOME_STRING, mockFieldValueSetter, SOME_OTHER_STRING, mockFieldValueSetter)).when(genericParameterisedJsonEndpoint).setters();
        doReturn(SOME_STRING).when(genericParameterisedJsonEndpoint).toString(testClass);

        Response response = genericParameterisedJsonEndpoint.put(EMPTY_MAP, Map.of(SOME_STRING, updatedVal, SOME_OTHER_STRING, otherUpdatedVal), mock(Headers.class));

        Assertions.assertEquals(ACCEPTED, response.getStatusCode());
        Assertions.assertEquals(SOME_STRING, response.getMessage());
        verify(mockFieldValueSetter).accept(testClass, updatedVal);
        verify(mockFieldValueSetter).accept(testClass, otherUpdatedVal);
        verify(mockDataAccessService).update(testClass);
    }

    @Test
    void get_throwsExceptionWhenServiceThrows() throws Exception {
        when(mockDataAccessService.get(anyMap())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(InternalServerErrorException.class, () -> genericParameterisedJsonEndpoint.get(EMPTY_MAP, EMPTY_MAP, mock(Headers.class)));
    }

    @Test
    void put_throwsExceptionWhenServiceThrows() throws Exception {
        when(mockDataAccessService.update(any())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(UnprocessableEntityException.class, () -> genericParameterisedJsonEndpoint.put(EMPTY_MAP, EMPTY_MAP, mock(Headers.class)));
    }

    @Test
    void delete_throwsExceptionWhenServiceThrows() throws Exception {
        doThrow(RuntimeException.class).when(mockDataAccessService).delete(any());

        Assertions.assertThrows(UnprocessableEntityException.class, () -> genericParameterisedJsonEndpoint.delete(EMPTY_MAP, SOME_REQUEST_BODY, mock(Headers.class)));
    }
}