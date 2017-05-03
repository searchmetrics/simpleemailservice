package com.searchmetrics.simpleEmailService;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class ServiceMetrics {
    private final Meter failedSendEmailRequests;
    private final Meter failedUploadAttachmentRequests;

    public ServiceMetrics(MetricRegistry metricRegistry) {
        this.failedSendEmailRequests = metricRegistry.meter("failed-sendemail-requests");
        this.failedUploadAttachmentRequests = metricRegistry.meter("failed-uploadattachment-requests");
    }

    public void markSendEmailException() {
        failedSendEmailRequests.mark();
    }

    public void markUploadAttachmentExceptions() {
        failedUploadAttachmentRequests.mark();
    }
}
