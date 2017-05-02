package com.searchmetrics.simpleEmailService;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class ServiceMetrics {
    private final Meter failedSendEmailRequests;

    public ServiceMetrics(MetricRegistry metricRegistry) {
        this.failedSendEmailRequests = metricRegistry.meter("failed-sendemail-requests");
    }

    public void markSendEmailException() {
        failedSendEmailRequests.mark();
    }
}
