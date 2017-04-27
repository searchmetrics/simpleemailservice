package com.searchmetrics.simpleEmailService.api.rest;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Path("/")
public class SimpleEmailServiceResource {
    final AWSCredentials CREDENTIALS;
    AmazonSimpleEmailServiceClient client;

    public SimpleEmailServiceResource() {
        try {
            CREDENTIALS = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.", e
            );
        }

        client = new AmazonSimpleEmailServiceClient(CREDENTIALS);
        Region REGION = Region.getRegion(Regions.EU_WEST_1);
        client.setRegion(REGION);
    }

    @POST
    @Path("sendEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(SendEmailRequest emailRequest) {
        // convert the parsed dto to a sendable email for AWS
        SendRawEmailRequest rawEmailRequest = emailRequest.toAWSRawEmailRequest();

        // send the email
        client.sendRawEmail(rawEmailRequest);

        // return 200 code and an answer that the email was sent
        return Response.ok().entity(new SendEmailResponse("Sent Email.")).build();
    }

    static class SendEmailResponse {
        private final String STATUS_MESSAGE;

        public SendEmailResponse(String statusMessage) {
            STATUS_MESSAGE = statusMessage;
        }

        @JsonProperty
        public String getStatusMessage() {
            return STATUS_MESSAGE;
        }
    }

    @GET
    @Path("helloWorld")
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld(@QueryParam("userName") final Optional<String> potentialUserName) {
            return Response.ok().entity(new HelloWorldResponse(potentialUserName, "Hello World, %s")).build();
    }

    static class HelloWorldResponse {
        private final String user;
        private final String msg;

        public HelloWorldResponse(Optional<String> user, String msg) {
            this.user = user.orElseGet(() -> "F2U");
            this.msg = String.format(msg, this.user);
        }

        @JsonProperty
        public String getUser() {
            return user;
        }

        @JsonProperty
        public String getMsg() {
            return msg;
        }
    }
}
