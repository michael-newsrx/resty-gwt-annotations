package com.newsrx.restygwt.util;

import com.google.gwt.http.client.Response;
import org.fusesource.restygwt.client.Method;

public class RestyResult<T> {
    public final Method method;
    public final T payload;
    public final Throwable exception;
    public final Response response;

    public RestyResult(Method method, T payload, Throwable exception, Response response) {
        this.method = method;
        this.payload = payload;
        this.exception = exception;
        this.response = response;
    }

    public boolean isSuccess() {
        return exception == null;
    }
}
