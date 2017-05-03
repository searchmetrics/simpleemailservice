package com.searchmetrics.simpleEmailService.dto;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.binary.Hex;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static jersey.repackaged.com.google.common.base.Preconditions.checkNotNull;

public class UploadAttachmentRequest {
    private static final String BUCKET_NAME = "simple-email-service-bucket";
    private String objectKey;
    private final String name;
    private final String mimeType;
    private final String data;

    @JsonCreator
    public UploadAttachmentRequest(
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

    public PutObjectRequest toPutObjectRequest() throws IOException, NoSuchAlgorithmException {
        //
        // Decode base64 data
        //

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = {};
        decodedBytes = decoder.decodeBuffer(this.data);

        InputStream inputStream = new ByteArrayInputStream(decodedBytes);


        //
        // Create a SHA-512 hash of the decoded bytes
        //

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] digest = messageDigest.digest(decodedBytes);

        // create a hexadecimal string and use it as name
        char[] hexDigest = Hex.encodeHex(digest);


        //
        // Create PutObjectRequest
        //

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(this.mimeType);
        metadata.setContentLength(decodedBytes.length);


        // the file
        // <SHA512>/<real file name>
        objectKey = String.valueOf(hexDigest);
        objectKey += "/";
        objectKey += this.name;

        PutObjectRequest putRequest = new PutObjectRequest(BUCKET_NAME, objectKey, inputStream, metadata);
        return putRequest;
    }

    public GeneratePresignedUrlRequest getPresignedUrlRequest() {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(BUCKET_NAME, checkNotNull(objectKey));
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);

        return generatePresignedUrlRequest;
    }
}