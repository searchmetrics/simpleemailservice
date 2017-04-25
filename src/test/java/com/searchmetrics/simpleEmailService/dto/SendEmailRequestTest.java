package com.searchmetrics.simpleEmailService.dto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class SendEmailRequestTest {
    private final static List<String> EMAIL_LIST = new ArrayList<String>(){
        {
            addAll(
                Arrays.asList(
                    "a.robinson@searchmetrics.com",
                    "linus.jahn@searchmetrics.com",
                    "p.pohlitz@searchmetrics.com"));
        }
    };

//    static {
//        someEmailAddresses.addAll(
//            Arrays.asList(
//                "a.robinson@searchmetrics.com",
//                "linus.jahn@searchmetrics.com",
//                "p.pohlitz@searchmetrics.com"));
//    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSendEmailRequestTest_InvalidConstructorCalls() {
        thrown.expect(IllegalArgumentException.class);
        SendEmailRequest sendEmailRequest = new SendEmailRequest(new ArrayList<>());

    }

    @Test
    public void testSendEmailRequestTest_ConstructorSuccess() {
        final SendEmailRequest sendEmailRequest = new SendEmailRequest(EMAIL_LIST);
        Assert.assertNotNull(sendEmailRequest);

        final List<String> toEmailList = sendEmailRequest.getToEmailList();
    }
}
