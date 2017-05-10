package com.searchmetrics.simpleEmailService.dto;

import com.amazonaws.services.simpleemail.model.GetSendStatisticsResult;
import com.amazonaws.services.simpleemail.model.SendDataPoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendStatisticsTest {
    @Test
    public void constructFromSendStatisticsResult() throws Exception {
        List<SendDataPoint> sendDataPointCollection = new ArrayList<>();

        SendDataPoint sendDataPointA = new SendDataPoint();
        sendDataPointA.setTimestamp(new Date(System.currentTimeMillis()));
        sendDataPointA.setDeliveryAttempts((long) 5);
        sendDataPointA.setBounces((long) 0);
        sendDataPointA.setComplaints((long) 0);
        sendDataPointA.setRejects((long) 0);

        SendDataPoint sendDataPointB = new SendDataPoint();
        sendDataPointB.setTimestamp(new Date(System.currentTimeMillis() - 10000));
        sendDataPointB.setBounces((long) 3);
        sendDataPointB.setComplaints((long) 2);
        sendDataPointB.setDeliveryAttempts((long) 500);
        sendDataPointB.setRejects((long) 2);

        sendDataPointCollection.add(sendDataPointA);
        sendDataPointCollection.add(sendDataPointB);

        GetSendStatisticsResult sendStatisticsResult = new GetSendStatisticsResult();
        sendStatisticsResult.setSendDataPoints(sendDataPointCollection);

        // convert it to SendStatistics
        SendStatistics sendStatistics = new SendStatistics(sendStatisticsResult);
        List<SendStatistics.DataPoint> dataPointList = sendStatistics.getDataPointList();

        Assert.assertEquals(2, dataPointList.size());

        SendStatistics.DataPoint dataPointA = dataPointList.get(0);
        SendStatistics.DataPoint dataPointB = dataPointList.get(1);

        Assert.assertEquals(sendDataPointA.getTimestamp(), dataPointA.getTimestamp());
        Assert.assertEquals(0, dataPointA.getBounces());
        Assert.assertEquals(0, dataPointA.getComplaints());
        Assert.assertEquals(5, dataPointA.getDeliveryAttempts());
        Assert.assertEquals(0, dataPointA.getRejects());

        Assert.assertEquals(sendDataPointB.getTimestamp(), dataPointB.getTimestamp());
        Assert.assertEquals(3, dataPointB.getBounces());
        Assert.assertEquals(2, dataPointB.getComplaints());
        Assert.assertEquals(500, dataPointB.getDeliveryAttempts());
        Assert.assertEquals(2, dataPointB.getRejects());
    }
}
