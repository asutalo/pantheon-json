package com.eu.at_it.json;


import com.eu.at_it.helper.Pair;
import com.eu.at_it.json.provider.EndpointFieldsProvider;
import com.eu.at_it.json.provider.functions.FieldValueSetter;
import com.eu.at_it.server.endpoint.Endpoint;
import com.eu.at_it.service.data.DataService;

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

    /**
     * TODO refactor so that EndpointFieldsProvider is not provided, instead it should be pulled from a singleton with a map of constructed extractor instances. The singleton constructs them via a Factory taking TypeLiteral<T>
     */
    public GenericJsonEndpoint(String uriDefinition, DataService<T, Q> service, EndpointFieldsProvider<T> endpointFieldsProvider) {
        super(uriDefinition);

        this.service = service;
        this.endpointFieldsProvider = endpointFieldsProvider;

        endpointFieldsProvider.init();
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
        return endpointFieldsProvider.locationGetter().apply(instance);
    }

    String withBracers(String join) {
        return "{\n\t" + join + "\n}";
    }

}
