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

public class UploadAttachmentMeta {
    private final String name;
    private final String mimeType;

    @JsonCreator
    public UploadAttachmentMeta(
            @JsonProperty("name") String name,
            @JsonProperty("mimeType") String mimeType
    ) {
        this.name = name;
        this.mimeType = mimeType;
    }

    @JsonProperty("name")
    public final String getName() {
        return name;
    }

    @JsonProperty("mimeType")
    public final String getMimeType() {
        return mimeType;
    }
}