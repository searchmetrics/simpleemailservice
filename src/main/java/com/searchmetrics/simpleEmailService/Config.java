package com.searchmetrics.simpleEmailService;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 *
 */
public class Config extends Configuration {
    public class SimpleEmailServiceConfig {
        private String fromEmailAddress = "";
        private String replyToEmailAddress = "";
        private boolean printOutgoingEmails = false;

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
    }

    private SimpleEmailServiceConfig simpleEmailService = new SimpleEmailServiceConfig();

    @JsonProperty("simpleEmailService")
    public SimpleEmailServiceConfig getSimpleEmailService() {
        return simpleEmailService;
    }

    @JsonProperty("simpleEmailService")
    public void setSimpleEmailService(SimpleEmailServiceConfig simpleEmailService) {
        this.simpleEmailService = simpleEmailService;
    }
}
