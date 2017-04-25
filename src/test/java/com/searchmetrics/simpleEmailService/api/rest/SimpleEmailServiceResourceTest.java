package com.searchmetrics.simpleEmailService.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 *
 */
public class SimpleEmailServiceResourceTest {
    private static final SimpleEmailServiceResource SIMPLE_EMAIL_SERVICE_RESOURCE =
        new SimpleEmailServiceResource();
    private static final ObjectMapper OM = Jackson.newObjectMapper();

    @Test
    public void helloWorld() throws Exception {
        Assert.assertNotNull(SIMPLE_EMAIL_SERVICE_RESOURCE.helloWorld(Optional.empty()));

//        Assert.assertEquals("F2U",
//            OM.readValue(SIMPLE_EMAIL_SERVICE_RESOURCE
//                .helloWorld(Optional.empty())
//                .getEntity(String.class),
//                SimpleEmailServiceResource.HelloWorldResponse.class)
//                .getUser()
//        );
    }
}