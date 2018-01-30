package com.vw.isms.web;

import com.fasterxml.jackson.annotation.JsonGetter;

public class ErrorResponse {
    private final String errorMsg;

    public ErrorResponse(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @JsonGetter("error_msg")
    public String getErrorMsg() {
        return this.errorMsg;
    }

    public boolean isSuccess() {
        return false;
    }
}
