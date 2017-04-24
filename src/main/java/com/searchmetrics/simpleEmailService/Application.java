package com.searchmetrics.simpleEmailService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmetrics.simpleEmailService.api.rest.SimpleEmailServiceResource;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;

/**
 *
 */
public class Application extends io.dropwizard.Application<Config> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        final ObjectMapper objectMapper = Jackson.newObjectMapper();
        final SimpleEmailServiceResource simpleEmailServiceResource =
            new SimpleEmailServiceResource(objectMapper);

        environment.jersey().register(simpleEmailServiceResource);
    }
}
