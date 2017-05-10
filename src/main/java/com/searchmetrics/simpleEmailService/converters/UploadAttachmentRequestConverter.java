package com.searchmetrics.simpleEmailService.converters;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.searchmetrics.simpleEmailService.Config;
import com.searchmetrics.simpleEmailService.dto.UploadAttachmentRequest;
import org.apache.commons.codec.binary.Hex;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UploadAttachmentRequestConverter {
    public static byte[] base64StringToByteArray(String base64Data) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64Data);
    }

    public static String generateSHA512HexDigest(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] digest = messageDigest.digest(data);

        // create a hexadecimal string and use it as name
        char[] hexDigest = Hex.encodeHex(digest);
        return String.valueOf(hexDigest);
    }

    public static ObjectMetadata generateObjectMetadata(String mimeType, int length) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(mimeType);
        metadata.setContentLength(length);
        return metadata;
    }

    public static PutObjectRequest toPutObjectRequest(UploadAttachmentRequest attachmentRequest, final Config CONFIG) throws IOException, NoSuchAlgorithmException {
        //
        // Decode base64 data (if needed)
        //

        if (attachmentRequest.getByteArrayData() == null) {
            attachmentRequest.setByteArrayData(base64StringToByteArray(attachmentRequest.getData()));
        }

        // create input stream from binary data
        InputStream inputStream = new ByteArrayInputStream(attachmentRequest.getByteArrayData());


        //
        // Create a SHA-512 hash of the decoded bytes
        //

        String hexDigest = generateSHA512HexDigest(attachmentRequest.getByteArrayData());


        //
        // Create PutObjectRequest
        //

        ObjectMetadata metadata = generateObjectMetadata(attachmentRequest.getMimeType(),
                attachmentRequest.getByteArrayData().length);


        // the file
        // <SHA512>/<real file name>
        String objectKey = String.format("%s/%s", hexDigest, attachmentRequest.getName());

        return new PutObjectRequest(CONFIG.getSimpleEmailServiceConfig().getS3BucketName(),
                objectKey, inputStream, metadata);
    }
}
