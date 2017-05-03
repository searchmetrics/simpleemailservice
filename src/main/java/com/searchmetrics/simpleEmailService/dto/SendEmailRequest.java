package com.searchmetrics.simpleEmailService.dto;

import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.searchmetrics.simpleEmailService.Config;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.ws.rs.InternalServerErrorException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class SendEmailRequest {
    public static class Attachment {
        private final String name;
        private final String mimeType;
        private final String data;

        @JsonCreator
        public Attachment(
                @JsonProperty("name") String name,
                @JsonProperty("mimeType") String mimeType,
                @JsonProperty("data") String data
        ) {
            this.name = name;
            this.mimeType = mimeType;
            this.data = data;
        }

        @JsonProperty("name")
        public final String getName() {
            return name;
        }

        @JsonProperty("mimeType")
        public final String getMimeType() {
            return mimeType;
        }

        @JsonProperty("data")
        public final String getData() {
            return data;
        }
    }

    private static Config config;

    private final static Session SESSION = Session.getDefaultInstance(new Properties());

    private final List<String> toEmailList;
    private final String subject;
    private final String messageBody;
    private final Optional<List<Attachment>> attachmentList;

    @JsonCreator
    public SendEmailRequest(
            @JsonProperty("toEmailList") List<String> toEmailList,
            @JsonProperty("subject") String subject,
            @JsonProperty("messageBody") String messageBody,
            @JsonProperty("attachmentList") Optional<List<Attachment>> attachmentList
    ) {
        this.toEmailList = checkNotNull(toEmailList);
        this.subject = checkNotNull(subject);
        this.messageBody = checkNotNull(messageBody);
        this.attachmentList = attachmentList;

        if (toEmailList.size() < 1) {
            throw new IllegalArgumentException("toEmailList must contain 1 or more values");
        }
    }

    @JsonProperty
    public List<String> getToEmailList() {
        return toEmailList;
    }

    @JsonProperty
    public String getSubject() {
        return subject;
    }

    @JsonProperty
    public String getMessageBody() {
        return messageBody;
    }

    @JsonProperty
    public Optional<List<Attachment>> getAttachmentList() {
        return attachmentList;
    }

    public static void setConfig(Config config) {
        SendEmailRequest.config = config;
    }

    public SendRawEmailRequest toAWSRawEmailRequest() throws IllegalArgumentException, InternalServerErrorException {
        // create a new message
        MimeMessage message = new MimeMessage(SESSION);

        //
        // Subject
        //

        try {
            message.setSubject(subject, "UTF-8");
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Invalid subject.");
        }


        //
        // From & Reply-To Address
        //

        try {
            message.setFrom(new InternetAddress(config.getSimpleEmailServiceConfig().getFromEmailAddress()));
            message.setReplyTo(new Address[]{new InternetAddress(config.getSimpleEmailServiceConfig().getReplyToEmailAddress())});
        } catch (Exception e) {
            throw  new InternalServerErrorException("Could not set From or Reply-To Address.");
        }


        //
        // To-Email Addresses
        //

        try {
            Iterator<String> iterator = toEmailList.iterator();
            while (iterator.hasNext()) {
                message.addRecipient(
                        Message.RecipientType.TO, // recipient type To
                        new InternetAddress(iterator.next())
                );
            }
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Invalid To-Address list.");
        }


        //
        // Create a multipart E-Mail
        //

        // Cover wrap
        MimeMultipart cover = new MimeMultipart("alternative");
        MimeBodyPart wrap = new MimeBodyPart();

        // full content (everything)
        MimeMultipart content = new MimeMultipart("related");
        // message body
        MimeBodyPart html = new MimeBodyPart();

        try {
            // add body to cover
            cover.addBodyPart(html);
            // add cover to wrap
            wrap.setContent(cover);

            // add full content to message
            message.setContent(content);
            // add wrap as body part to full content
            content.addBodyPart(wrap);
        } catch (MessagingException e) {
            throw new InternalServerErrorException("Could not create multipart E-Mail");
        }


        //
        // Message body
        //

        try {
            html.setContent(messageBody, "text/html");
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Invalid messageBody.");
        }


        //
        // Add all existing attachments
        //

        if (attachmentList.isPresent()) {
            for (Attachment localAttachment : attachmentList.get()) {
                // generate a new random UUID
                String id = UUID.randomUUID().toString();

                // Base64 data
                MimeBodyPart attachment = new PreencodedMimeBodyPart("base64");
                try {
                    attachment.setText(localAttachment.getData());
                } catch (MessagingException e) {
                    throw new IllegalArgumentException("Invalid attachment data. (Is it base64?)");
                }

                // Set the Mime-Type
                try {
                    attachment.setHeader("Content-Type", localAttachment.getMimeType());
                } catch (MessagingException e) {
                    throw new IllegalArgumentException("Invalid mimeType.");
                }

                // Set the Content UUID
                try {
                    attachment.setHeader("Content-ID", "<" + id + ">");
                } catch (MessagingException e) {
                    throw new InternalServerErrorException("Invalid content-id in attachment.");
                }

                // Set the file name
                try {
                    attachment.setFileName(localAttachment.getName());
                } catch (MessagingException e) {
                    throw new IllegalArgumentException("Invalid attachment file name.");
                }

                // Add the attachment
                try {
                    content.addBodyPart(attachment);
                } catch (MessagingException e) {
                    throw new InternalServerErrorException("Could not add attachment to message.");
                }
            }
        }


        // Optionally print raw email to the console
        if (config.getSimpleEmailServiceConfig().getPrintOutgoingEmails()) {
            try {
                message.writeTo(System.out);
            } catch (Exception e) {}
        }


        //
        // Create the raw message & email request
        //

        // Write raw E-Mail to output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            message.writeTo(outputStream);
        } catch (Exception e) {
            throw new InternalServerErrorException("Could write raw E-Mail to output stream.");
        }

        // Create a AWS raw message and send request from the output stream
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        SendRawEmailRequest emailRequest = new SendRawEmailRequest(rawMessage);

        // return the AWS SendRawEmailRequest
        return emailRequest;
    }
}
