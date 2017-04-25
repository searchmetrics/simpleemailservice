package com.searchmetrics.simpleEmailService.api.rest;

import java.util.Optional;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

import static org.junit.Assert.*;

/**
 *
 */
public class SimpleEmailServiceResourceTest {
    SimpleEmailServiceResource serviceResource;

    @org.junit.Before
    public void setUp() throws Exception {
        final ObjectMapper objectMapper = Jackson.newObjectMapper();
        serviceResource = new SimpleEmailServiceResource(objectMapper);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void helloWorld() throws Exception {
//        final Optional<String> userName = Optional.of("");
//        Response response = serviceResource.helloWorld(userName);
    }
}