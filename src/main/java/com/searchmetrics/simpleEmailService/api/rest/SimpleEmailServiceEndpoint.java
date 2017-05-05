package com.searchmetrics.simpleEmailService.api.rest;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.GetSendStatisticsRequest;
import com.amazonaws.services.simpleemail.model.GetSendStatisticsResult;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.SendEmailRequestConverter;
import com.searchmetrics.simpleEmailService.ServiceMetrics;
import com.searchmetrics.simpleEmailService.dto.SendEmailRequest;
import com.searchmetrics.simpleEmailService.dto.SendStatistics;
import com.searchmetrics.simpleEmailService.dto.UploadAttachmentRequest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;

/**
 *
 */
@Path("/")
public class SimpleEmailServiceEndpoint {
    private ServiceMetrics serviceMetrics;
    private Config config;
    private final AWSCredentials CREDENTIALS;
    private AmazonSimpleEmailServiceClient sesClient;
    private AmazonS3 s3Client;

    public SimpleEmailServiceEndpoint(ServiceMetrics serviceMetrics, Config config) {
        this.serviceMetrics = serviceMetrics;
        this.config = config;

        try {
            CREDENTIALS = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.", e
            );
        }

        // the region for all AWS clients
        final Region REGION_S3 = Region.getRegion(Regions.EU_CENTRAL_1);
        final Region REGION_SES = Region.getRegion(Regions.EU_WEST_1);

        // AWS SimpleEMailService Client
        sesClient = new AmazonSimpleEmailServiceClient(CREDENTIALS);
        sesClient.setRegion(REGION_SES);

        // AWS SimpleStorageService Client
        s3Client = new AmazonS3Client(CREDENTIALS);
        s3Client.setRegion(REGION_S3);
    }

    @POST
    @Path("sendEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(SendEmailRequest emailRequest) {
        try {
            // convert the parsed dto to a sendable email for AWS
            SendRawEmailRequest rawEmailRequest = SendEmailRequestConverter.toAWSSendRawEmailRequest(emailRequest, config);

            // send the email
            sesClient.sendRawEmail(rawEmailRequest);
        } catch (Exception e) {
            serviceMetrics.markSendEmailException();

            return Response
                    .serverError()
                    .entity(new SendEmailResponse("E-Mail was not send: " + e.getMessage()))
                    .build();
        }

        // return 200 code
        return Response.ok().entity(new SendEmailResponse("E-Mail was sent.")).build();
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

    @POST
    @Path("uploadAttachmentJson")
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAttachment(UploadAttachmentRequest uploadRequest) {
        try {
            // Upload the attachment
            PutObjectRequest putRequest = uploadRequest.toPutObjectRequest();
            PutObjectResult putResult = s3Client.putObject(putRequest);

            // Get the url for downloads
            URL url = s3Client.getUrl(config.getSimpleEmailServiceConfig().getS3BucketName(), uploadRequest.getFileKey());

            return Response.ok().entity(new UploadAttachmentResponse("Uploaded attachment.", url.toString())).build();
        } catch (Exception e) {
            serviceMetrics.markUploadAttachmentExceptions();
            System.out.println(e.getMessage());
            e.printStackTrace();

            return Response.serverError().entity(new UploadAttachmentResponse("Not uploaded: " + e.getMessage(), "")).build();
        }
    }

    @POST
    @Path("uploadAttachmentBin")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAttachmentMultipart(
            @FormDataParam("attachment") InputStream inputStream,
            @FormDataParam("attachment") FormDataContentDisposition contentDispositionHeader
    ) {
        try {
            // create UploadAttachmentRequest from binary file
            UploadAttachmentRequest uploadRequest = new UploadAttachmentRequest(
                    contentDispositionHeader.getFileName(), contentDispositionHeader.getType(), inputStream);

            // use normal upload attachment function
            return uploadAttachment(uploadRequest);
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new UploadAttachmentResponse("Not uploaded: " + e.getMessage(), ""))
                    .build();
        }
    }

    static class UploadAttachmentResponse {
        private final String STATUS_MESSAGE;
        private final String URL;

        public UploadAttachmentResponse(String statusMessage, String url) {
            STATUS_MESSAGE = statusMessage;
            URL = url;
        }

        @JsonProperty("statusMessage")
        public String getStatusMessage() {
            return STATUS_MESSAGE;
        }

        @JsonProperty("url")
        public final String getUrl() {
            return URL;
        }
    }


    @GET
    @Path("sendStatistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSendStatistics() {
        //
        // Get send statistics from AWS
        //

        GetSendStatisticsRequest statsRequest = new GetSendStatisticsRequest();
        GetSendStatisticsResult statsResponse = sesClient.getSendStatistics(statsRequest);

        // Create a new send statistics dto from the response
        SendStatistics sendStatistics = new SendStatistics(statsResponse);

        return Response.ok().entity(sendStatistics).build();
    }
}
