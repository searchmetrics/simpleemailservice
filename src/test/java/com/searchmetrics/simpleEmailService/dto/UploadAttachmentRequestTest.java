package com.searchmetrics.simpleEmailService.dto;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class UploadAttachmentRequestTest {
    @Test
    public void testConstructorAndGetters() {
        UploadAttachmentRequest attachmentRequest = new UploadAttachmentRequest(
                "file-name.txt", "text/plain",
                "VGhpcyBpcyB0aGUgYmVzdCBhdHRhY2htZW50IGV2ZXIgYmVlbiBzZW50IQo="
        );

        Assert.assertEquals("file-name.txt", attachmentRequest.getName());
        Assert.assertEquals("text/plain", attachmentRequest.getMimeType());
        Assert.assertEquals("VGhpcyBpcyB0aGUgYmVzdCBhdHRhY2htZW50IGV2ZXIgYmVlbiBzZW50IQo=", attachmentRequest.getData());
    }

    @Test
    public void testConstructorAndGettersUsingInputStream() throws IOException {
        UploadAttachmentRequest attachmentRequest = new UploadAttachmentRequest(
                "file-name.txt", "text/plain",
                new ByteArrayInputStream("This is the best attachment ever been sent!".getBytes())
        );

        Assert.assertEquals("file-name.txt", attachmentRequest.getName());
        Assert.assertEquals("text/plain", attachmentRequest.getMimeType());

        byte[] binData = "This is the best attachment ever been sent!".getBytes();
        byte[] binDataFromAttachment = attachmentRequest.getByteArrayData();

        Assert.assertEquals(binData.length, binDataFromAttachment.length);
        for (int i = 0; i < binData.length; i++) {
            Assert.assertEquals(binData[i], binDataFromAttachment[i]);
        }

    }
}