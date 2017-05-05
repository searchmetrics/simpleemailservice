package com.searchmetrics.simpleEmailService;

import com.codahale.metrics.MetricRegistry;
import com.searchmetrics.simpleEmailService.api.rest.SimpleEmailServiceEndpoint;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;
import com.searchmetrics.simpleEmailService.dto.UploadAttachmentRequest;
import com.searchmetrics.simpleEmailService.util.LogFilter;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 */
public class SimpleEmailApplication extends Application<Config> {
    private final ApplicationContext beans;

    public SimpleEmailApplication() {
        super();
        beans = new AnnotationConfigApplicationContext(SpringConfig.class);
    }

    public static void main(String[] args) throws Exception {
        new SimpleEmailApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        final MetricRegistry registry = this.beans.getBean(MetricRegistry.class);

        bootstrap.setMetricRegistry(registry);
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        SimpleEmailServiceEndpoint.setConfig(config);
        SendEmailRequest.setConfig(config);
        UploadAttachmentRequest.setConfig(config);

        final SimpleEmailServiceEndpoint serviceResource = this.beans.getBean(SimpleEmailServiceEndpoint.class);

        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(LogFilter.class);
        environment.jersey().register(serviceResource);
    }
}
