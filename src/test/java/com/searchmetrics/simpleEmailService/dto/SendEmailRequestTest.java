package com.searchmetrics.simpleEmailService.dto;

import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.converters.SendEmailRequestConverter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class SendEmailRequestTest {
    private final Config CONFIG;
    private final static List<String> EMAIL_LIST = new ArrayList<String>(){
        {
            addAll(
                Arrays.asList(
                    "a.robinson@searchmetrics.com",
                    "linus.jahn@searchmetrics.com",
                    "p.pohlitz@searchmetrics.com"));
        }
    };

    public SendEmailRequestTest() {
        Config config = new Config();
        Config.SimpleEmailServiceConfig emailServiceConfig = new Config.SimpleEmailServiceConfig();
        emailServiceConfig.setFromEmailAddress("linus.jahn@searchmetrics.com");
        emailServiceConfig.setReplyToEmailAddress("linus.jahn@searchmetrics.com");
        emailServiceConfig.setPrintOutgoingEmails(false);
        config.setSimpleEmailServiceConfig(emailServiceConfig);

        CONFIG = config;
    }

    @Test
    public void testConstructorAndGettersWithoutAttachments() {
        SendEmailRequest emailRequest = new SendEmailRequest(EMAIL_LIST, "[Service Test] This is a subject",
                "This is the message body. Hello!", null);


        Assert.assertEquals(EMAIL_LIST, emailRequest.getToEmailList());
        Assert.assertEquals("[Service Test] This is a subject", emailRequest.getSubject());
        Assert.assertEquals("This is the message body. Hello!", emailRequest.getMessageBody());
    }

    @Test
    public void testMultipleAttachments() {
        List<SendEmailRequest.Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(new SendEmailRequest.Attachment("myFileName.txt", "text/plain",
                "SSB3YXMgYmFzZTY0LCBidXQgbm93IEknbSBwbGFpbiB0ZXh0IQ=="));
        attachmentList.add(new SendEmailRequest.Attachment("another_file.png", "image/png",
                "iVBORw0KGgoAAAANSUhEUgAAACQAAAAQCAIAAADxiUp0AAAACXBIWXMAAA7EAAAOxAGVKw4bAAAD6ElEQVQ4jb2UX0xbVRzHf+feS3vvbVcLreLGWOkf/m5r6ZhhWZw4eNiMSwCf1cQXfcGZLLqYLZk8OaNhmuHUQXALZr7oEl1xD2ZCxkjIYNjbTaFcVv4U2lF6b8taKO3tPff4UMVogg8k8/dyzvl+Hz4553vyRYQQ+L+G2cogWNHmB/HSCGV20NWvAMNi8Udt5T5V6qVdL6EiwzZgaKub5QNXiDSJuBKipIEQVMSTfAbpzSSbQGxx0eH3twGjtjJIcgYH+shGAt+/qoWHtZiA/Zchv46FXi3m3wbpv2AIFV4YAQAwemC4zRPQ+u3BECGEqFkcvk0eL9AVzcjshPw6XryjRccBK5S1jihp2t4CNIfnfkZcCWRXqYpmylS+DRhD8hl14hJZj4GaU1fnKdtRbWEQsAIEA8Mhc8WkbDj3xqlcNtd14dP+vqvJ1aQs/bCrrEyW5EtffHn27Jm52dm2tvZ9+/d3X/xMkqSPP+m6du2bUCh08uQ7wWDwtwcPRHG6p7fPbrdTgBX8+7d4ZgAYPfZ/pcUELPTiqe9Ab8L+y5ocBIDW1naWZYeHbweDwYMHn5ubm29sPCQI/lTqcX19/QtNTdevf+/1epuaXkQIyZLk/9Xf0fH2+fMfhkKhp8xmi8U6MXEP/soMgaaCmi1kBQCAc4DzBevWL7dGRu6wHItVDADV1dU8z9XU1Oj1bDi8eOXrPlavx1h9FI12d198973TiqLwBn7nzl3pVErN550Oh/VpK8YYACjQGYuOfkRXtSKzk/a+iWgd3dBBuU6gYhdz+AztPB6Px2Ox5ZVYbGFhfjPpwrK2lk6lUkJAkCTJN+CzWK0XurpySm4lttL5wbljx46zHPvvD1LY4chdLN4g6SWk24GKHUWNpwq6oiji9LTd4QCARCKRz+czmUylyxWJRm02WygUslosNMMYjQaKogEgODX1TGnp0tKi2+2Jx+M6nS6bzfI8bzKZ/m4QLTKKhR7m0Gl14nMA2ISNjo6yLBt6+DAuSeHwAsdxi+Ew9fIJSZbGx8c8nnrfgG/v3n2iKBqNhtrauqGhQbfHE41ENE0LBAINBxrGxu62tbfDP+sKAcFESYOSBp1pU52ZEQOCoBHNaDC6XK7J2dlXX3vd57thtVgzG5nenh63x33z5k9Hnj8yOTW5/GhZluV74+PNLS39/f1Op3NwaDCvKIlE0mKx0p2dnX+i2GLQVLRjN2gqc+At6llvQec4PrmaZFnO7XavJpNOpysQEGpr6yiaikSW7BX27MZGWdnuPTZbVVW14PdbLBae50VRdLpc62trlZVVsiyVlJSUl+/ZshufxGxZV09i/gDl8uDxBTmWkwAAAABJRU5ErkJggg=="));


        SendEmailRequest emailRequest = new SendEmailRequest(EMAIL_LIST, "This is a subject",
                "This is the message body. Hello!", attachmentList);

        Assert.assertEquals(attachmentList, emailRequest.getAttachmentList());
    }
}
