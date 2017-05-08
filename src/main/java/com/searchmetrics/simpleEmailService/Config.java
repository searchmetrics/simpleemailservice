package com.searchmetrics.simpleEmailService;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 *
 */
public class Config extends Configuration {
    public static class SimpleEmailServiceConfig {
        private String fromEmailAddress = "";
        private String replyToEmailAddress = "";
        private boolean printOutgoingEmails = false;
        private String s3BucketName = "";

        @JsonProperty("fromEmailAddress")
        public String getFromEmailAddress() {
            return fromEmailAddress;
        }
        @JsonProperty("fromEmailAddress")
        public void setFromEmailAddress(String fromEmailAddress) {
            this.fromEmailAddress = fromEmailAddress;
        }

        @JsonProperty("replyToEmailAddress")
        public String getReplyToEmailAddress() {
            return replyToEmailAddress;
        }
        @JsonProperty("replyToEmailAddress")
        public void setReplyToEmailAddress(String replyToEmailAddress) {
            this.replyToEmailAddress = replyToEmailAddress;
        }

        @JsonProperty("printOutgoingEmails")
        public boolean getPrintOutgoingEmails() {
            return printOutgoingEmails;
        }
        @JsonProperty("printOutgoingEmails")
        public void setPrintOutgoingEmails(boolean printOutgoingEmails) {
            this.printOutgoingEmails = printOutgoingEmails;
        }

        @JsonProperty("s3BucketName")
        public String getS3BucketName() {
            return s3BucketName;
        }
        @JsonProperty("s3BucketName")
        public void setS3BucketName(String s3BucketName) {
            this.s3BucketName = s3BucketName;
        }
    }

    private SimpleEmailServiceConfig simpleEmailService = new SimpleEmailServiceConfig();

    @JsonProperty("simpleEmailService")
    public SimpleEmailServiceConfig getSimpleEmailServiceConfig() {
        return simpleEmailService;
    }

    @JsonProperty("simpleEmailService")
    public void setSimpleEmailServiceConfig(SimpleEmailServiceConfig simpleEmailServiceConfig) {
        this.simpleEmailService = simpleEmailServiceConfig;
    }
}
