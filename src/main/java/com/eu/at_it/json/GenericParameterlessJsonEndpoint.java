package com.eu.at_it.json;

import com.eu.at_it.json.provider.EndpointFieldsProvider;
import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.server.response.Response;
import com.eu.at_it.pantheon.server.response.SimpleResponse;
import com.eu.at_it.pantheon.server.response.SimpleResponseWithHeaders;
import com.eu.at_it.pantheon.server.response.exception.InternalServerErrorException;
import com.eu.at_it.pantheon.service.data.DataService;
import com.sun.net.httpserver.Headers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Basic generic endpoint to provide GET (all), and POST verbs
 */
public class GenericParameterlessJsonEndpoint<T, Q> extends GenericJsonEndpoint<T, Q> {
    private String locationRoot;

    public GenericParameterlessJsonEndpoint(String uriDefinition, DataService<T, Q> service, String locationRoot, EndpointFieldsProvider<T> endpointFieldsProvider) {
        super(uriDefinition, service, endpointFieldsProvider);
        setLocationRoot(locationRoot);
    }

    /**
     * Fetches all elements of the specified type
     */
    @Override
    public Response get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            List<T> instances = service.getAll();

            List<String> formattedStrings = instances.stream().map(this::toString).collect(Collectors.toList());
            String join = String.join(",\n\t", formattedStrings);

            return new SimpleResponse(OK, "[\n\t" + join + "\n]");
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Used to register a new user.
     * Responds with the newly created user ID in the body and the uri to the fully detailed user in the "location" header
     */
    @Override
    public Response post(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        T instance = service.instanceOfT(requestBody);

        try {
            T savedUser = service.save(instance);

            Pair<String, String> locationPair = getLocation(savedUser);

            new HashMap<>();

            return new SimpleResponseWithHeaders(CREATED, String.valueOf(withBracers(locationPair.left())), Map.of(LOCATION, List.of(locationRoot + locationPair.right())));

        } catch (Exception e) {
            throw new InternalServerErrorException(); //todo proper exception response code
        }
    }

    private void setLocationRoot(String locationRoot) {
        if (!locationRoot.endsWith("/")) {
            locationRoot += "/";
        }

        this.locationRoot = locationRoot;
    }
}
