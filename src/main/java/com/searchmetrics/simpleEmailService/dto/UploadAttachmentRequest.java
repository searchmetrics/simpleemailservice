package com.searchmetrics.simpleEmailService.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.io.InputStream;

public class UploadAttachmentRequest {
    private final String name;
    private final String mimeType;
    private String data;
    private byte[] binData;

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

    public UploadAttachmentRequest(String name, String mimeType, InputStream inputStream) throws IOException {
        this.name = name;
        this.mimeType = mimeType;
        this.binData = org.apache.commons.io.IOUtils.toByteArray(inputStream);
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

    public byte[] getByteArrayData() {
        return binData;
    }

    public void setByteArrayData(byte[] binData) {
        this.binData = binData;
    }
}