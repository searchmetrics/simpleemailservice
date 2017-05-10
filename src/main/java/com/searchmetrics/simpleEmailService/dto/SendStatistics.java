package com.searchmetrics.simpleEmailService.dto;

import com.amazonaws.services.simpleemail.model.GetSendStatisticsResult;
import com.amazonaws.services.simpleemail.model.SendDataPoint;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendStatistics {
    class DataPoint {
        final Date timestamp;
        final long deliveryAttempts;
        final long bounces;
        final long complaints;
        final long rejects;

        public DataPoint(
                @JsonProperty("timestamp") Date timestamp,
                @JsonProperty("deliveryAttempts") long deliveryAttempts,
                @JsonProperty("bounces") long bounces,
                @JsonProperty("complaints") long complaints,
                @JsonProperty("rejects") long rejects
        ) {
            this.timestamp = timestamp;
            this.deliveryAttempts = deliveryAttempts;
            this.bounces = bounces;
            this.complaints = complaints;
            this.rejects = rejects;
        }

        @JsonProperty("timestamp")
        public Date getTimestamp() {
            return timestamp;
        }
        @JsonProperty("deliveryAttempts")
        public long getDeliveryAttempts() {
            return deliveryAttempts;
        }
        @JsonProperty("bounces")
        public long getBounces() {
            return bounces;
        }
        @JsonProperty("complaints")
        public long getComplaints() {
            return complaints;
        }
        @JsonProperty("rejects")
        public long getRejects() {
            return rejects;
        }
    }

    private final List<DataPoint> dataPointList;

    @JsonCreator
    public SendStatistics(
            @JsonProperty("dataPoints") List<DataPoint> dataPointList
    ) {
        this.dataPointList = dataPointList;
    }

    public SendStatistics(GetSendStatisticsResult statsResult) {
        List<DataPoint> dataPoints = new ArrayList<>();

        for (SendDataPoint element : statsResult.getSendDataPoints()) {
            dataPoints.add(new DataPoint(
                    element.getTimestamp(),
                    element.getDeliveryAttempts(),
                    element.getBounces(),
                    element.getComplaints(),
                    element.getRejects()
            ));
        }

        this.dataPointList = dataPoints;
    }

    @JsonProperty("dataPoints")
    public List<DataPoint> getDataPointList() {
        return dataPointList;
    }
}
