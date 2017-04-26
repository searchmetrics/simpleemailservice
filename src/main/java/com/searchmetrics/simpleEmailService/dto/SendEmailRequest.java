package com.searchmetrics.simpleEmailService.dto;

import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javassist.bytecode.ByteArray;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class SendEmailRequest {
    private final static String FROM_EMAIL = "noreply@dev.searchmetrics.space";
    private final static String EMAIL_REPLY_TO = "noreply@dev.searchmetrics.space";

    private final List<String> toEmailList;
    private final String subject;
    private final String messageBody;
    private final Optional<List<Attachment>> optionalAttachmentList;

    public static class Attachment {
        private final String name;
        private final String mimeType;
        private final String data;

        @JsonCreator
        public Attachment(
                @JsonProperty String name,
                @JsonProperty String mimeType,
                @JsonProperty String data
        ) {
            this.name = name;
            this.mimeType = mimeType;
            this.data = data;
        }

        @JsonProperty
        public final String getName() {
            return name;
        }

        @JsonProperty
        public final String getMimeType() {
            return mimeType;
        }

        @JsonProperty
        public final String getData() {
            return data;
        }
    }

    @JsonCreator
    public SendEmailRequest(
            @JsonProperty List<String> toEmailList,
            @JsonProperty String subject,
            @JsonProperty String messageBody,
            @JsonProperty Optional<List<Attachment>> optionalAttachmentList
    ) {
        this.toEmailList = checkNotNull(toEmailList);
        this.subject = checkNotNull(subject);
        this.messageBody = checkNotNull(messageBody);
        this.optionalAttachmentList = optionalAttachmentList;

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

    public SendRawEmailRequest toRawEmailRequest() throws RuntimeException {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);

            message.setSubject(subject, "UTF-8");
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setReplyTo(new Address[]{new InternetAddress(EMAIL_REPLY_TO)});

            Iterator<String> iterator = toEmailList.iterator();
            while (iterator.hasNext()) {
                message.addRecipient(
                        Message.RecipientType.TO, // recipient type To
                        new InternetAddress(iterator.next())
                );
            }

            // Cover wrap
            MimeBodyPart wrap = new MimeBodyPart();

            // Alternative TEXT/HTML content
            MimeMultipart cover = new MimeMultipart("alternative");
            MimeBodyPart html = new MimeBodyPart();
            cover.addBodyPart(html);

            wrap.setContent(cover);

            MimeMultipart content = new MimeMultipart("related");
            message.setContent(content);
            content.addBodyPart(wrap);

            html.setContent("<html><body><h1>HTML</h1>\n" + messageBody + "</body></html>", "text/html");

            //
            // Add all existing attachments
            //

            if (optionalAttachmentList.isPresent()) {
                for (Attachment localAttachment : optionalAttachmentList.get()) {
                    String id = UUID.randomUUID().toString();

                    MimeBodyPart attachment = new MimeBodyPart();

                    ByteArrayDataSource bds = new ByteArrayDataSource(localAttachment.getData(), localAttachment.getMimeType());
                    attachment.setDataHandler(new DataHandler(bds));
                    attachment.setHeader("Content-ID", "<" + id + ">");
                    attachment.setFileName(localAttachment.getName());

                    content.addBodyPart(attachment);
                }
            }

            // print raw email to the console
            message.writeTo(System.out);

            //
            // Create a the raw message & email request
            //


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest emailRequest = new SendRawEmailRequest(rawMessage);

            return emailRequest;
        } catch (MessagingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }





//    @JsonProperty
//    public Optional<List<Attachment>> getOptionalAttachmentList() {
//        return optionalAttachmentList;
//    }

    //    @JsonCreator
//    public SendEmailRequest(
//            final List<String> emailList) throws IllegalArgumentException {
//        optioanlAttachmentList = new ArrayList<Attachment>();
//
//        toEmailList = checkNotNull(emailList);
//        if (toEmailList.size() < 1) {
//            throw new IllegalArgumentException("toEmailList must contain 1 or more values");
//        }
//
//    }
//    public void addAttachment(String name, String mimeType, String data) throws RuntimeException {
//        if (!attachmentListContainsName(name)) {
//            optioanlAttachmentList.add(new Attachment(name, mimeType, data));
//        } else {
//            throw new RuntimeException();
//        }
//    }
//
//    private boolean attachmentListContainsName(String name) {
//        Iterator<Attachment> iterator = optioanlAttachmentList.iterator();
//        // find attachment that has this name
//        while (iterator.hasNext()) {
//            Attachment attachment = iterator.next();
//            if (attachment.getName() == name) {
//                return true;
//            }
//        }
//        // list doesn't contain attachment with this name
//        return false;
//    }
//
//    public String getAttachmentMimeTypeByName(String name) {
//        Iterator<Attachment> iterator = optioanlAttachmentList.iterator();
//        // find attachment that has this name
//        while (iterator.hasNext()) {
//            Attachment attachment = iterator.next();
//            if (attachment.getName() == name) {
//                return attachment.getMimeType();
//            }
//        }
//
//        // unsuccessful
//        return "";
//    }
//
//    public String getAttachmentDataByName(String name) {
//        Iterator<Attachment> iterator = optioanlAttachmentList.iterator();
//        // find attachment that has this name
//        while (iterator.hasNext()) {
//            Attachment attachment = iterator.next();
//            if (attachment.getName() == name) {
//                return attachment.getData();
//            }
//        }
//
//        // unsuccessful
//        return "";
//    }
}
