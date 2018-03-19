package com.vw.isms.web.response;

public class GenericResponse {
    private final boolean success;
    private final String message;

    private GenericResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public static GenericResponse success() {
        return new GenericResponse(true, "ok");
    }

    public static GenericResponse fail(String message) {
        return new GenericResponse(false, message);
    }
}
