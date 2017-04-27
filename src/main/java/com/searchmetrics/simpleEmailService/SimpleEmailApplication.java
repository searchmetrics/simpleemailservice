package com.searchmetrics.simpleEmailService;

import com.searchmetrics.simpleEmailService.api.rest.SimpleEmailServiceResource;
import com.searchmetrics.simpleEmailService.util.LogFilter;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 *
 */
public class SimpleEmailApplication extends Application<Config> {

    public static void main(String[] args) throws Exception {
        new SimpleEmailApplication().run(args);
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        environment.jersey().register(LogFilter.class);
        environment.jersey().register(SimpleEmailServiceResource.class);
    }
}
