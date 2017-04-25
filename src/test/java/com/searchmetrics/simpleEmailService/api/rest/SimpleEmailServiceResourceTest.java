package com.searchmetrics.simpleEmailService.api.rest;

import java.util.Optional;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SimpleEmailServiceResourceTest {
    SimpleEmailServiceResource serviceResource;

    @Before
    public void setUp() throws Exception {
        final ObjectMapper objectMapper = Jackson.newObjectMapper();
        serviceResource = new SimpleEmailServiceResource(objectMapper);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void helloWorld() throws Exception {
//        final Optional<String> userName = Optional.of("");
//        Response response = serviceResource.helloWorld(userName);
    }
}