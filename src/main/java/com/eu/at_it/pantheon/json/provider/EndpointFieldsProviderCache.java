package com.eu.at_it.pantheon.json.provider;

import com.google.inject.TypeLiteral;

import java.util.HashMap;
import java.util.Map;

public class EndpointFieldsProviderCache {
    private static EndpointFieldsProviderCache INSTANCE;

    private final Map<TypeLiteral<?>, EndpointFieldsProvider<?>> cache = new HashMap<>();

    private EndpointFieldsProviderCache() {
    }

    public static synchronized EndpointFieldsProviderCache INSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new EndpointFieldsProviderCache();
        }

        return INSTANCE;
    }

    static void resetINSTANCE() {
        INSTANCE = null;
    }

    //for easier testing
    public static void setInstance(EndpointFieldsProviderCache INSTANCE) {
        EndpointFieldsProviderCache.INSTANCE = INSTANCE;
    }

    static EndpointFieldsProviderCache getINSTANCE() {
        return INSTANCE;
    }

    public <T> EndpointFieldsProvider<T> endpointFieldsProviderFor(TypeLiteral<T> tTypeLiteral) {
        if (cache.containsKey(tTypeLiteral)) {
            return (EndpointFieldsProvider<T>) cache.get(tTypeLiteral);
        } else {
            EndpointFieldsProvider<T> tEndpointFieldsProvider = endpointFieldsProvider(tTypeLiteral);

            cache.put(tTypeLiteral, tEndpointFieldsProvider);

            tEndpointFieldsProvider.init();

            return tEndpointFieldsProvider;
        }
    }

    //below methods are here just for sake of unit tests
    <T> EndpointFieldsProvider<T> endpointFieldsProvider(TypeLiteral<T> tTypeLiteral) {
        return new EndpointFieldsProvider<>(tTypeLiteral);
    }

    Map<TypeLiteral<?>, EndpointFieldsProvider<?>> getCache() {
        return cache;
    }
}
