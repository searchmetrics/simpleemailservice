package com.searchmetrics.simpleEmailService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.searchmetrics.simpleEmailService.api.rest.SimpleEmailServiceResource;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;
import com.searchmetrics.simpleEmailService.util.LogFilter;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import jdk.nashorn.internal.parser.JSONParser;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.io.File;

/**
 *
 */
public class SimpleEmailApplication extends Application<Config> {

    public static void main(String[] args) throws Exception {
        new SimpleEmailApplication().run(args);
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        SendEmailRequest.setConfig(config);

        environment.jersey().register(LogFilter.class);
        environment.jersey().register(SimpleEmailServiceResource.class);
    }
}
