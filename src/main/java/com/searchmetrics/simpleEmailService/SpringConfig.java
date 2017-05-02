package com.searchmetrics.simpleEmailService;

import com.codahale.metrics.MetricRegistry;
import com.searchmetrics.simpleEmailService.api.rest.SimpleEmailServiceEndpoint;
import org.springframework.context.annotation.Bean;

public class SpringConfig {
    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public ServiceMetrics serviceMetrics(final MetricRegistry metricRegistry) {
        return new ServiceMetrics(metricRegistry);
    }

    @Bean
    public SimpleEmailServiceEndpoint simpleEmailServiceEndpoint(final ServiceMetrics serviceMetrics) {
        return new SimpleEmailServiceEndpoint(serviceMetrics);
    }
}