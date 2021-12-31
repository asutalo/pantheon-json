package com.eu.at_it.pantheon.json.provider;

import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

//apparently I really, really like seeing 100% code coverage...
@ExtendWith(MockitoExtension.class)
class EndpointFieldsProviderCacheTest {
    @Mock
    private EndpointFieldsProvider<Object> mockEndpointFieldsProvider;

    @Test
    void instance() {
        EndpointFieldsProviderCache.resetINSTANCE();
        Assertions.assertNull(EndpointFieldsProviderCache.getINSTANCE());
        EndpointFieldsProviderCache instance = EndpointFieldsProviderCache.INSTANCE();
        Assertions.assertNotNull(instance);
        Assertions.assertEquals(instance, EndpointFieldsProviderCache.INSTANCE());
    }

    @Test
    void shouldReturnNewProvider() {
        EndpointFieldsProviderCache.resetINSTANCE();
        EndpointFieldsProviderCache endpointFieldsProviderCache = spy(EndpointFieldsProviderCache.INSTANCE());

        doReturn(mockEndpointFieldsProvider).when(endpointFieldsProviderCache).endpointFieldsProvider(any());

        TypeLiteral<Object> tTypeLiteral = TypeLiteral.get(Object.class);

        EndpointFieldsProvider<Object> actual = endpointFieldsProviderCache.endpointFieldsProviderFor(tTypeLiteral);
        endpointFieldsProviderCache.endpointFieldsProviderFor(tTypeLiteral); // to check caching works

        verify(endpointFieldsProviderCache).endpointFieldsProvider(tTypeLiteral);
        verify(mockEndpointFieldsProvider).init();
        Assertions.assertEquals(mockEndpointFieldsProvider, actual);
        Assertions.assertEquals(mockEndpointFieldsProvider, endpointFieldsProviderCache.getCache().get(tTypeLiteral));
    }

    @Test
    void endpointFieldsProvider() {
        Assertions.assertNotNull(EndpointFieldsProviderCache.INSTANCE().endpointFieldsProvider(TypeLiteral.get(Object.class)));
    }
}