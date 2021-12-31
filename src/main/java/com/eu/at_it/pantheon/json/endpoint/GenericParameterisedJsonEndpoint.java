package com.eu.at_it.pantheon.json.endpoint;

import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.server.response.Response;
import com.eu.at_it.pantheon.server.response.SimpleResponse;
import com.eu.at_it.pantheon.server.response.exception.InternalServerErrorException;
import com.eu.at_it.pantheon.server.response.exception.UnprocessableEntityException;
import com.eu.at_it.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;
import com.sun.net.httpserver.Headers;

import java.util.Map;

/**
 * Basic generic endpoint to provide GET by param, PUT, and DELETE verbs
 */
public class GenericParameterisedJsonEndpoint<T, Q> extends GenericJsonEndpoint<T, Q> {
    public GenericParameterisedJsonEndpoint(String uriDefinition, DataService<T, Q> service, TypeLiteral<T> typeLiteral) {
        super(uriDefinition, service, typeLiteral);
    }

    /**
     * Fetches the element specified in query path
     */
    @Override
    public Response get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            T instance = service.get(uriParams);

            return new SimpleResponse(OK, toString(instance));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Deletes the element specified in query path
     */
    @Override
    public Response delete(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            T toDelete = service.get(uriParams);

            service.delete(toDelete);

            Pair<String, String> locationPair = getLocation(toDelete);

            return new SimpleResponse(ACCEPTED, String.valueOf(withBracers(locationPair.left())));
        } catch (Exception e) {
            throw new UnprocessableEntityException(); //todo proper exception response code
        }
    }

    /**
     * Updates the element specified in query path
     */
    @Override
    public Response put(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            T toUpdate = service.get(uriParams);

            requestBody.keySet().forEach(key -> {
                if (setters().containsKey(key)) setters().get(key).accept(toUpdate, requestBody.get(key));
            });

            service.update(toUpdate);

            return new SimpleResponse(ACCEPTED, toString(toUpdate));

        } catch (Exception e) {
            throw new UnprocessableEntityException(); //todo proper exception response code
        }
    }
}
