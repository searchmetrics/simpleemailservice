package com.searchmetrics.simpleEmailService.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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

    private final List<String> toEmailList;
    private final String subject;
    private final String messageBody;
    private final List<Attachment> attachmentList;

    @JsonCreator
    public SendEmailRequest(
            @JsonProperty("toEmailList") List<String> toEmailList,
            @JsonProperty("subject") String subject,
            @JsonProperty("messageBody") String messageBody,
            @JsonProperty("attachmentList") List<Attachment> attachmentList
    ) {
        this.toEmailList = checkNotNull(toEmailList);
        this.subject = checkNotNull(subject);
        this.messageBody = checkNotNull(messageBody);
        this.attachmentList = attachmentList == null ? new ArrayList<Attachment>() : attachmentList;

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
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }
}
