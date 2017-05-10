package com.searchmetrics.simpleEmailService.converters;

import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class SendEmailRequestConverter {
    public static SendRawEmailRequest toAWSSendRawEmailRequest(
            com.searchmetrics.simpleEmailService.dto.SendEmailRequest emailRequest,
            Config config
    ) throws MessagingException, IOException {
        // create a new Message
        Message mail = createNewMessage(config);

        //
        // Message Structure
        //

        MimeMultipart cover = new MimeMultipart("alternative");
        MimeBodyPart wrap = new MimeBodyPart();
        MimeMultipart content = new MimeMultipart("related"); // full content (everything)
        MimeBodyPart html = new MimeBodyPart(); // message body
        cover.addBodyPart(html); // add body to cover
        wrap.setContent(cover); // add cover to wrap
        mail.setContent(content); // add full content to message
        content.addBodyPart(wrap); // add wrap as body part to full content

        // set fields from JSON
        setMessageSubject(mail, emailRequest.getSubject());
        setMessageToEmails(mail, emailRequest.getToEmailList());
        setMessageBody(html, emailRequest.getMessageBody());
        addAttachments(content, emailRequest.getAttachmentList());

        // Optionally print raw email to the console
        if (config.getSimpleEmailServiceConfig().getPrintOutgoingEmails()) {
            mail.writeTo(System.out);
        }

        return messageToRawEmailRequest(mail);
    }

    public static Message createNewMessage(Config config) {
        // create a new message
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));

        // From & Reply-To Address
        try {
            message.setFrom(new InternetAddress(config.getSimpleEmailServiceConfig().getFromEmailAddress()));
            message.setReplyTo(new Address[]{new InternetAddress(config.getSimpleEmailServiceConfig().getReplyToEmailAddress())});
        } catch (Exception e) {
            throw new IllegalStateException("Config: Reply-To or From Address not set");
        }

        return message;
    }

    public static void setMessageSubject(Message mail, String subject) throws MessagingException {
        mail.setSubject(subject);
    }

    public static void setMessageToEmails(Message mail, List<String> toEmailList) {
        for (String recipient : toEmailList) {
            try {
                mail.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress(recipient, true)
                );
            } catch (MessagingException e) {
                throw new IllegalArgumentException(recipient + " is not a valid E-Mail Address.");
            }
        }
    }

    public static void setMessageBody(MimeBodyPart html, String body) throws MessagingException {
        html.setContent(body, "text/html");
    }

    //
    // Adds a list of attachments
    //
    public static void addAttachments(
            MimeMultipart content, List<com.searchmetrics.simpleEmailService.dto.SendEmailRequest.Attachment> attachmentList
    ) throws MessagingException {
        for (SendEmailRequest.Attachment attachmentJson : attachmentList) {
            // generate a new random UUID
            String id = UUID.randomUUID().toString();

            // Base64 data
            MimeBodyPart attachment = new PreencodedMimeBodyPart("base64");
            attachment.setText(attachmentJson.getData());

            // Set the Mime-Type
            attachment.setHeader("Content-Type", attachmentJson.getMimeType());

            // Set the Content UUID
            attachment.setHeader("Content-ID", "<" + id + ">");

            // Set the file name
            attachment.setFileName(attachmentJson.getName());

            // Add the attachment
            content.addBodyPart(attachment);
        }
    }

    //
    // Creates a raw email request
    //
    public static SendRawEmailRequest messageToRawEmailRequest(Message mail) throws IOException, MessagingException {
        // Write raw E-Mail to output stream
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mail.writeTo(outputStream);

        // Create a AWS raw message and send request from the output stream
        final RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

        return new SendRawEmailRequest(rawMessage);
    }
}
