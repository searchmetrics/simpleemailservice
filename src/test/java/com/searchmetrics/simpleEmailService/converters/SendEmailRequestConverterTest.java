package com.searchmetrics.simpleEmailService.converters;

import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.converters.SendEmailRequestConverter;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.mail.*;
import javax.mail.internet.*;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.*;

public class SendEmailRequestConverterTest {
    private final Config CONFIG;
    private final static List<String> EMAIL_LIST = new ArrayList<String>() {
        {
            addAll(Arrays.asList("akrobinson74@gmail.com", "categorical@rocketmail.com"));
        }
    };

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    public SendEmailRequestConverterTest() {
        Config config = new Config();
        Config.SimpleEmailServiceConfig emailServiceConfig = new Config.SimpleEmailServiceConfig();
        emailServiceConfig.setPrintOutgoingEmails(false);
        emailServiceConfig.setReplyToEmailAddress("noreply@searchmetrics.com");
        emailServiceConfig.setFromEmailAddress("noreply@searchmetrics.com");
        config.setSimpleEmailServiceConfig(emailServiceConfig);

        CONFIG = config;
    }

    @Test
    public void createNewMessage() throws Exception {
        Message mail = SendEmailRequestConverter.createNewMessage(CONFIG);

        Assert.assertEquals(CONFIG.getSimpleEmailServiceConfig().getFromEmailAddress(), mail.getFrom()[0].toString());
        Assert.assertEquals(CONFIG.getSimpleEmailServiceConfig().getReplyToEmailAddress(), mail.getReplyTo()[0].toString());
    }

    @Test
    public void setMessageSubject() throws Exception {
        Message mail = new MimeMessage(Session.getDefaultInstance(new Properties()));

        final String exampleSubject = "This is an example subject!";
        SendEmailRequestConverter.setMessageSubject(mail, exampleSubject);

        Assert.assertEquals(mail.getSubject(), exampleSubject);
    }

    @Test
    public void setMessageToEmails() throws Exception {
        Message mail = new MimeMessage(Session.getDefaultInstance(new Properties()));

        SendEmailRequestConverter.setMessageToEmails(mail, EMAIL_LIST);

        List<InternetAddress> addresssList = new ArrayList<>();
        for (String email : EMAIL_LIST) {
            addresssList.add(new InternetAddress(email));
        }

        Assert.assertArrayEquals(addresssList.toArray(), mail.getAllRecipients());

        List<Address> recipients = Arrays.asList(mail.getAllRecipients());
        for (String mailAddress : EMAIL_LIST) {
            if (!recipients.contains(new InternetAddress(mailAddress))) {
                fail();
            }
        }
    }

    @Test
    public void setMessageToEmailsWithNotValidEMail() throws Exception {
        Message mail = new MimeMessage(Session.getDefaultInstance(new Properties()));
        List<String> toEmailList = new ArrayList<>();
        toEmailList.add("i-am-valid@very-valid.com");
        toEmailList.add("I'm not a valid E-Mail address! :P");

        thrown.expect(IllegalArgumentException.class);
        SendEmailRequestConverter.setMessageToEmails(mail, toEmailList);
    }

    @Test
    public void setMessageBody() throws Exception {
        MimeBodyPart bodyPart = new MimeBodyPart();
        final String exampleMessageBody = "I'm an example of a message body.";

        SendEmailRequestConverter.setMessageBody(bodyPart, exampleMessageBody);
        Assert.assertEquals(bodyPart.getContent(), exampleMessageBody);
    }

    @Test
    public void addAttachments() throws Exception {
        MimeMultipart multipart = new MimeMultipart();

        List<SendEmailRequest.Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(new SendEmailRequest.Attachment("a-file-name.txt", "text/plain", "SGVsbG8gV29ybGQh"));

        SendEmailRequestConverter.addAttachments(multipart, attachmentList);

        // check if size is correct
        Assert.assertEquals(multipart.getCount(), attachmentList.size());

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            String name = bodyPart.getFileName();
            String mimeType = bodyPart.getHeader("Content-Type")[0].split(";")[0];
            String data = bodyPart.getContent().toString();

            Boolean attachmentExistsInList = false;
            for (SendEmailRequest.Attachment attachmentJson : attachmentList) {
                if (name.equals(attachmentJson.getName()) && mimeType.equals(attachmentJson.getMimeType()) &&
                        data.equals(attachmentJson.getData())) {
                    attachmentExistsInList = true;
                }
            }

            if (!attachmentExistsInList) {
                fail();
            }
        }
    }

    @Test
    public void messageToRawEmailRequest() throws Exception {
        Message mail = SendEmailRequestConverter.createNewMessage(CONFIG);
        mail.setText("This is the content of this mail!");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mail.writeTo(outputStream);

        SendRawEmailRequest mailRequest = SendEmailRequestConverter.messageToRawEmailRequest(mail);
        RawMessage rawMessage = mailRequest.getRawMessage();

        Assert.assertEquals(ByteBuffer.wrap(outputStream.toByteArray()), rawMessage.getData());
    }
}