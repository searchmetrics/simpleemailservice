package com.searchmetrics.simpleEmailService.converters;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.dto.UploadAttachmentRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.fail;

public class UploadAttachmentRequestConverterTest {
    private final Config CONFIG;

    public UploadAttachmentRequestConverterTest() {
        Config config = new Config();
        Config.SimpleEmailServiceConfig emailServiceConfig = new Config.SimpleEmailServiceConfig();
        emailServiceConfig.setS3BucketName("this-is-not-the-real-bucket");
        config.setSimpleEmailServiceConfig(emailServiceConfig);

        CONFIG = config;
    }

    @Test
    public void base64StringToByteArray() throws Exception {
        String base64Data = "SGVsbG8gV29ybGQuIFRoaXMgd2lsbCBiZSBCYXNlNjQgZW5jb2RlZC4K";
        byte[] decodedData = "Hello World. This will be Base64 encoded.".getBytes();
        byte[] decodedDataFromConverter = UploadAttachmentRequestConverter.base64StringToByteArray(base64Data);

        for (int i = 0; i < decodedData.length; i++) {
            Assert.assertEquals(decodedData[i], decodedDataFromConverter[i]);
        }
    }

    @Test
    public void generateSHA512HexDigest() throws Exception {
        byte[] data = "A checksum for this will be generated.".getBytes();
        String sha512Checksum = "983aa9c966517958961fa559f35498ad21be07ba2d41aba9a4c4d0d53fde5c0d4ab63def7314283e3ca01ed0aefc6a23e1220363688b083bd841a8ba7281d07e";

        Assert.assertEquals(sha512Checksum, UploadAttachmentRequestConverter.generateSHA512HexDigest(data));
    }

    @Test
    public void generateObjectMetadata() throws Exception {
        ObjectMetadata metadata = UploadAttachmentRequestConverter.generateObjectMetadata("text/plain", 20151021);
        Assert.assertEquals("text/plain", metadata.getContentType());
        Assert.assertEquals(20151021, metadata.getContentLength());
    }

    @Test
    public void toPutObjectRequest() throws Exception {
        String base64String = "SGVsbG8gV29ybGQuIFRoaXMgaXMgYSB0ZXh0Lgo=";
        UploadAttachmentRequest attachmentRequest = new UploadAttachmentRequest("hello-world.txt", "text/plain", base64String);

        String objectKey = String.format("%s/%s",
                UploadAttachmentRequestConverter.generateSHA512HexDigest(UploadAttachmentRequestConverter.base64StringToByteArray(base64String)),
                "hello-world.txt"
        );

        PutObjectRequest putRequest = UploadAttachmentRequestConverter.toPutObjectRequest(attachmentRequest, CONFIG);
        InputStream inputStream = putRequest.getInputStream();
        ObjectMetadata metadataFromPut = putRequest.getMetadata();

        Assert.assertEquals(CONFIG.getSimpleEmailServiceConfig().getS3BucketName(), putRequest.getBucketName());
        Assert.assertEquals(objectKey, putRequest.getKey());

        Assert.assertEquals("text/plain", metadataFromPut.getContentType());
        Assert.assertEquals(UploadAttachmentRequestConverter.base64StringToByteArray(base64String).length, metadataFromPut.getContentLength());

        if (! IOUtils.contentEquals(
                new ByteArrayInputStream(UploadAttachmentRequestConverter.base64StringToByteArray(base64String)),
                inputStream
        )) {
            fail();
        }
    }
}