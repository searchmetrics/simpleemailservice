package com.searchmetrics.simpleEmailService.dto;

import java.lang.IllegalArgumentException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class SendEmailRequest {
    private final List<String> toEmailList;

    public SendEmailRequest(final List<String> emailList) throws IllegalArgumentException {
        toEmailList = checkNotNull(emailList);

        if (toEmailList.size() < 1) {
            throw new IllegalArgumentException("toEmailList must contain 1 or more values");
        }

    }

    public List<String> getToEmailList() {
        return toEmailList;
    }

}
