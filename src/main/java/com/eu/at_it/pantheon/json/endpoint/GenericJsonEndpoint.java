package com.eu.at_it.pantheon.json.endpoint;


import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProvider;
import com.eu.at_it.pantheon.json.provider.EndpointFieldsProviderCache;
import com.eu.at_it.pantheon.json.provider.functions.FieldValueSetter;
import com.eu.at_it.pantheon.json.provider.functions.ValueJsonValuePair;
import com.eu.at_it.pantheon.server.endpoint.Endpoint;
import com.eu.at_it.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class GenericJsonEndpoint<T, Q> extends Endpoint {
    static final int OK = 200;
    static final int CREATED = 201;
    static final int ACCEPTED = 202;
    static final String LOCATION = "Location";
    final DataService<T, Q> service;
    private final EndpointFieldsProvider<T> endpointFieldsProvider;

    public GenericJsonEndpoint(String uriDefinition, DataService<T, Q> service, TypeLiteral<T> typeLiteral) {
        super(uriDefinition);
        this.service = service;
        this.endpointFieldsProvider = EndpointFieldsProviderCache.INSTANCE().endpointFieldsProviderFor(typeLiteral);
    }

    String toString(T instance) {
        List<String> variables = endpointFieldsProvider.fieldGetters().values().stream().map(getter -> getter.apply(instance)).collect(Collectors.toList());
        String join = String.join(",\n\t", variables);
        return withBracers(join);
    }

    Map<String, FieldValueSetter<T>> setters() {
        return endpointFieldsProvider.fieldSetters();
    }

    Pair<String, String> getLocation(T instance) {
        ValueJsonValuePair<T> tValueJsonValuePair = endpointFieldsProvider.locationGetter();
        return tValueJsonValuePair.apply(instance);
    }

    String withBracers(String join) {
        return "{\n\t" + join + "\n}";
    }

}
