package com.searchmetrics.simpleEmailService.api.rest;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;
import io.dropwizard.jackson.Jackson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

/**
 *
 */
public class SimpleEmailServiceEndpointTest {
    @Test
    public void testSendEmail() {

    }

}