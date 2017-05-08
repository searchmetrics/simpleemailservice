package com.searchmetrics.simpleEmailService;

import com.codahale.metrics.MetricRegistry;
import com.searchmetrics.simpleEmailService.api.rest.SimpleEmailServiceEndpoint;
import com.searchmetrics.simpleEmailService.util.LogFilter;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 */
public class SimpleEmailApplication extends Application<Config> {
    private final MetricRegistry metricRegistry;
    private final ServiceMetrics serviceMetrics;

    public SimpleEmailApplication() {
        metricRegistry = new MetricRegistry();
        serviceMetrics = new ServiceMetrics(metricRegistry);
    }

    public static void main(String[] args) throws Exception {
        new SimpleEmailApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.setMetricRegistry(metricRegistry);
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        final SimpleEmailServiceEndpoint serviceResource = new SimpleEmailServiceEndpoint(serviceMetrics, config);

        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(LogFilter.class);
        environment.jersey().register(serviceResource);
    }
}
