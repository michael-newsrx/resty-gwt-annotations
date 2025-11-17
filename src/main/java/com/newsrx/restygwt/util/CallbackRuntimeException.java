package com.newsrx.restygwt.util;

import java.util.Collections;
import java.util.Map;

public class CallbackRuntimeException extends RuntimeException {

    private final String text;
    private final Map<String, String> headers;
    private final int statusCode;
    private final String statusText;

    public CallbackRuntimeException(String message, Throwable cause, String text, Map<String, String> headers,
                                    int statusCode, String statusText) {
        super(message, cause);
        this.text = text;
        this.headers = Collections.unmodifiableMap(headers);
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static CallbackRuntimeException create(String message, Throwable cause, String text,
                                                  Map<String, String> headers, int statusCode, String statusText) {
        return new CallbackRuntimeException(message, cause, text, headers, statusCode, statusText);
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
