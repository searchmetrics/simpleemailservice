package com.searchmetrics.simpleEmailService.util;

import org.glassfish.jersey.message.internal.ReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class LogFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("Request Body: {}", readEntityStream( requestContext ));
    }

    private static String readEntityStream(
        ContainerRequestContext requestContext) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final InputStream inputStream = requestContext.getEntityStream();
        final StringBuilder builder = new StringBuilder();
        try
        {
            ReaderWriter.writeTo(inputStream, outStream);
            byte[] requestEntity = outStream.toByteArray();
            if (requestEntity.length == 0) {
                builder.append("");
            } else {
                builder.append(new String(requestEntity));
            }
            requestContext.setEntityStream(
                new ByteArrayInputStream(requestEntity) );
        } catch (IOException ex) {
            LOGGER.debug("Exception occurred while reading entity stream :{}",
                ex.getMessage());
        }
        return builder.toString();
    }
}
