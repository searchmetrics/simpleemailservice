package com.searchmetrics.simpleEmailService.api.rest;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.ServiceMetrics;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;
import com.searchmetrics.simpleEmailService.dto.SendStatistics;
import com.searchmetrics.simpleEmailService.dto.UploadAttachmentRequest;
import org.apache.commons.validator.UrlValidator;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 *
 */
public class SimpleEmailServiceEndpointTest {

    private final SimpleEmailServiceEndpoint endpoint;
    private final static List<String> EMAIL_LIST = new ArrayList<String>(){
        {
            addAll(Arrays.asList("akrobinson74@gmail.com", "categorical@rocketmail.com"));
        }
    };

    public SimpleEmailServiceEndpointTest() {
        Config config = new Config();
        Config.SimpleEmailServiceConfig emailServiceConfig = new Config.SimpleEmailServiceConfig();
        emailServiceConfig.setPrintOutgoingEmails(false);
        emailServiceConfig.setReplyToEmailAddress("a.robinson@searchmetrics.com");
        emailServiceConfig.setFromEmailAddress("a.robinson@searchmetrics.com");
        emailServiceConfig.setS3BucketName("simple-email-service-bucket");
        config.setSimpleEmailServiceConfig(emailServiceConfig);

        this.endpoint = new SimpleEmailServiceEndpoint(new ServiceMetrics(new MetricRegistry()), config);
    }

    @Test
    public void sendEmailEmptyAttachmentList() throws Exception {
        SendEmailRequest emailRequest = new SendEmailRequest(
                EMAIL_LIST, "[Service Test] The coolest subject ever!", "The best message body for a test.", null);

        Response response = endpoint.sendEmail(emailRequest);
        SimpleEmailServiceEndpoint.SendEmailResponse emailResponse = (SimpleEmailServiceEndpoint.SendEmailResponse) response.getEntity();

        Assert.assertEquals("E-Mail was sent.", emailResponse.getStatusMessage());
    }

    @Test
    public void sendEmailWithAttachments() throws Exception {
        List<SendEmailRequest.Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(new SendEmailRequest.Attachment("file-name.txt", "text/plain", "VGhpcyBpcyB0aGUgYXR0YWNobWVudC4K"));
        attachmentList.add(new SendEmailRequest.Attachment("aws.png", "image/png", "iVBORw0KGgoAAAANSUhEUgAAACQAAAAQCAIAAADxiUp0AAAACXBIWXMAAA7EAAAOxAGVKw4bAAAD6ElEQVQ4jb2UX0xbVRzHf+feS3vvbVcLreLGWOkf/m5r6ZhhWZw4eNiMSwCf1cQXfcGZLLqYLZk8OaNhmuHUQXALZr7oEl1xD2ZCxkjIYNjbTaFcVv4U2lF6b8taKO3tPff4UMVogg8k8/dyzvl+Hz4553vyRYQQ+L+G2cogWNHmB/HSCGV20NWvAMNi8Udt5T5V6qVdL6EiwzZgaKub5QNXiDSJuBKipIEQVMSTfAbpzSSbQGxx0eH3twGjtjJIcgYH+shGAt+/qoWHtZiA/Zchv46FXi3m3wbpv2AIFV4YAQAwemC4zRPQ+u3BECGEqFkcvk0eL9AVzcjshPw6XryjRccBK5S1jihp2t4CNIfnfkZcCWRXqYpmylS+DRhD8hl14hJZj4GaU1fnKdtRbWEQsAIEA8Mhc8WkbDj3xqlcNtd14dP+vqvJ1aQs/bCrrEyW5EtffHn27Jm52dm2tvZ9+/d3X/xMkqSPP+m6du2bUCh08uQ7wWDwtwcPRHG6p7fPbrdTgBX8+7d4ZgAYPfZ/pcUELPTiqe9Ab8L+y5ocBIDW1naWZYeHbweDwYMHn5ubm29sPCQI/lTqcX19/QtNTdevf+/1epuaXkQIyZLk/9Xf0fH2+fMfhkKhp8xmi8U6MXEP/soMgaaCmi1kBQCAc4DzBevWL7dGRu6wHItVDADV1dU8z9XU1Oj1bDi8eOXrPlavx1h9FI12d198973TiqLwBn7nzl3pVErN550Oh/VpK8YYACjQGYuOfkRXtSKzk/a+iWgd3dBBuU6gYhdz+AztPB6Px2Ox5ZVYbGFhfjPpwrK2lk6lUkJAkCTJN+CzWK0XurpySm4lttL5wbljx46zHPvvD1LY4chdLN4g6SWk24GKHUWNpwq6oiji9LTd4QCARCKRz+czmUylyxWJRm02WygUslosNMMYjQaKogEgODX1TGnp0tKi2+2Jx+M6nS6bzfI8bzKZ/m4QLTKKhR7m0Gl14nMA2ISNjo6yLBt6+DAuSeHwAsdxi+Ew9fIJSZbGx8c8nnrfgG/v3n2iKBqNhtrauqGhQbfHE41ENE0LBAINBxrGxu62tbfDP+sKAcFESYOSBp1pU52ZEQOCoBHNaDC6XK7J2dlXX3vd57thtVgzG5nenh63x33z5k9Hnj8yOTW5/GhZluV74+PNLS39/f1Op3NwaDCvKIlE0mKx0p2dnX+i2GLQVLRjN2gqc+At6llvQec4PrmaZFnO7XavJpNOpysQEGpr6yiaikSW7BX27MZGWdnuPTZbVVW14PdbLBae50VRdLpc62trlZVVsiyVlJSUl+/ZshufxGxZV09i/gDl8uDxBTmWkwAAAABJRU5ErkJggg=="));

        SendEmailRequest emailRequest = new SendEmailRequest(
                EMAIL_LIST, "[Service Test] The coolest subject ever!", "The best message body for a test.", attachmentList);

        Response response = endpoint.sendEmail(emailRequest);
        SimpleEmailServiceEndpoint.SendEmailResponse emailResponse = (SimpleEmailServiceEndpoint.SendEmailResponse) response.getEntity();

        Assert.assertEquals("E-Mail was sent.", emailResponse.getStatusMessage());
    }

    @Test
    public void uploadAttachmentJsonText() throws Exception {
        UploadAttachmentRequest attachmentRequest = new UploadAttachmentRequest("a-cool-name.txt", "text/plain", "VGhpcyBpcyB0aGUgYXR0YWNobWVudC4K");

        Response response = endpoint.uploadAttachment(attachmentRequest);
        SimpleEmailServiceEndpoint.UploadAttachmentResponse attachmentResponse =
                (SimpleEmailServiceEndpoint.UploadAttachmentResponse) response.getEntity();

        Assert.assertEquals("Uploaded attachment.", attachmentResponse.getStatusMessage());

        UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
        if (!urlValidator.isValid(attachmentResponse.getUrl())) {
            fail("UploadAttachmentResponse returned a non-valid url!");
        }
    }

    @Test
    public void uploadAttachmentJsonPNG() throws Exception {
        UploadAttachmentRequest attachmentRequest = new UploadAttachmentRequest("aws.png", "image/png", "iVBORw0KGgoAAAANSUhEUgAAACQAAAAQCAIAAADxiUp0AAAACXBIWXMAAA7EAAAOxAGVKw4bAAAD6ElEQVQ4jb2UX0xbVRzHf+feS3vvbVcLreLGWOkf/m5r6ZhhWZw4eNiMSwCf1cQXfcGZLLqYLZk8OaNhmuHUQXALZr7oEl1xD2ZCxkjIYNjbTaFcVv4U2lF6b8taKO3tPff4UMVogg8k8/dyzvl+Hz4553vyRYQQ+L+G2cogWNHmB/HSCGV20NWvAMNi8Udt5T5V6qVdL6EiwzZgaKub5QNXiDSJuBKipIEQVMSTfAbpzSSbQGxx0eH3twGjtjJIcgYH+shGAt+/qoWHtZiA/Zchv46FXi3m3wbpv2AIFV4YAQAwemC4zRPQ+u3BECGEqFkcvk0eL9AVzcjshPw6XryjRccBK5S1jihp2t4CNIfnfkZcCWRXqYpmylS+DRhD8hl14hJZj4GaU1fnKdtRbWEQsAIEA8Mhc8WkbDj3xqlcNtd14dP+vqvJ1aQs/bCrrEyW5EtffHn27Jm52dm2tvZ9+/d3X/xMkqSPP+m6du2bUCh08uQ7wWDwtwcPRHG6p7fPbrdTgBX8+7d4ZgAYPfZ/pcUELPTiqe9Ab8L+y5ocBIDW1naWZYeHbweDwYMHn5ubm29sPCQI/lTqcX19/QtNTdevf+/1epuaXkQIyZLk/9Xf0fH2+fMfhkKhp8xmi8U6MXEP/soMgaaCmi1kBQCAc4DzBevWL7dGRu6wHItVDADV1dU8z9XU1Oj1bDi8eOXrPlavx1h9FI12d198973TiqLwBn7nzl3pVErN550Oh/VpK8YYACjQGYuOfkRXtSKzk/a+iWgd3dBBuU6gYhdz+AztPB6Px2Ox5ZVYbGFhfjPpwrK2lk6lUkJAkCTJN+CzWK0XurpySm4lttL5wbljx46zHPvvD1LY4chdLN4g6SWk24GKHUWNpwq6oiji9LTd4QCARCKRz+czmUylyxWJRm02WygUslosNMMYjQaKogEgODX1TGnp0tKi2+2Jx+M6nS6bzfI8bzKZ/m4QLTKKhR7m0Gl14nMA2ISNjo6yLBt6+DAuSeHwAsdxi+Ew9fIJSZbGx8c8nnrfgG/v3n2iKBqNhtrauqGhQbfHE41ENE0LBAINBxrGxu62tbfDP+sKAcFESYOSBp1pU52ZEQOCoBHNaDC6XK7J2dlXX3vd57thtVgzG5nenh63x33z5k9Hnj8yOTW5/GhZluV74+PNLS39/f1Op3NwaDCvKIlE0mKx0p2dnX+i2GLQVLRjN2gqc+At6llvQec4PrmaZFnO7XavJpNOpysQEGpr6yiaikSW7BX27MZGWdnuPTZbVVW14PdbLBae50VRdLpc62trlZVVsiyVlJSUl+/ZshufxGxZV09i/gDl8uDxBTmWkwAAAABJRU5ErkJggg==");

        Response response = endpoint.uploadAttachment(attachmentRequest);
        SimpleEmailServiceEndpoint.UploadAttachmentResponse attachmentResponse =
                (SimpleEmailServiceEndpoint.UploadAttachmentResponse) response.getEntity();

        Assert.assertEquals("Uploaded attachment.", attachmentResponse.getStatusMessage());

        UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
        if (!urlValidator.isValid(attachmentResponse.getUrl())) {
            fail("Returned url is not valid.");
        }
    }

    @Test
    public void uploadAttachmentBinary() throws Exception {
        String message = "Hello, this attachment was transmitted without base64 encoding.";
        InputStream stream = new ByteArrayInputStream(message.getBytes());

        String header = "form-data; filename=\"my-text.txt\"; name=\"attachment\"";
        FormDataContentDisposition contentDisposition = new FormDataContentDisposition(header);

        Response response = endpoint.uploadAttachmentBinary(stream, contentDisposition);
        SimpleEmailServiceEndpoint.UploadAttachmentResponse attachmentResponse =
                (SimpleEmailServiceEndpoint.UploadAttachmentResponse) response.getEntity();

        Assert.assertEquals("Uploaded attachment.", attachmentResponse.getStatusMessage());
        UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
        if (!urlValidator.isValid(attachmentResponse.getUrl())) {
            fail("Returned url is not valid.");
        }
    }

    @Test
    public void getSendStatistics() throws Exception {
        // This is just testing if getSendStatistics is returning SendStatistics,
        // because the data point list can be empty, if there was no activity in
        // the last two weeks.
        Response response = endpoint.getSendStatistics();
        SendStatistics sendStatistics = (SendStatistics) response.getEntity();
    }
}