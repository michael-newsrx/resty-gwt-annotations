/*
 * Attribution: Generated for Michael (NewSRX Tech) by Microsoft Copilot
 * Context: ButterApiApplication (Spring Boot 3.x, MVC migration, JDBC session management)
 * Timestamp (EST5EDT): 2025-11-17 12:50:00
 */

package com.newsrx.restygwt.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.function.Consumer;
import java.util.function.Function;

public class RestyPromise<T> implements MethodCallback<T> {

    private Function<RestyResult<T>, ?> successHandler;
    private Function<RestyResult<T>, ?> failureHandler;

    /**
     * Chainable transformable then clause.
     * Allows transforming RestyResult<T> into RestyResult<U>.
     */
    public <U> RestyPromise<U> map(Function<RestyResult<T>, RestyResult<U>> handler) {
        RestyPromise<U> next = new RestyPromise<>();
        this.successHandler = result -> {
            RestyResult<U> transformed = handler.apply(result);
            if (transformed.isSuccess()) {
                next.onSuccess(transformed.method, transformed.payload);
            } else {
                next.onFailure(transformed.method, transformed.exception);
            }
            return transformed;
        };
        this.failureHandler = result -> {
            // propagate failure downstream unchanged
            next.onFailure(result.method, result.exception);
            return result;
        };
        return next;
    }

    @Override
    public void onSuccess(Method method, T response) {
        RestyResult<T> result = new RestyResult<>(method, response, null, method.getResponse());
        if (successHandler != null) {
            successHandler.apply(result);
        }
    }

    @Override
    public void onFailure(Method method, Throwable exception) {
        RestyResult<T> result =
                new RestyResult<>(method, null, exception, method != null ? method.getResponse() : null);
        if (failureHandler != null) {
            failureHandler.apply(result);
        }
    }

    /**
     * Non-transforming then clause.
     * Consumes the result without returning a new value.
     */
    public RestyPromise<T> then(Consumer<RestyResult<T>> handler) {
        RestyPromise<T> next = new RestyPromise<>();
        this.successHandler = result -> {
            handler.accept(result);
            next.onSuccess(result.method, result.payload);
            return result;
        };
        this.failureHandler = result -> {
            next.onFailure(result.method, result.exception);
            return result;
        };
        return next;
    }

    /**
     * Chainable transformable catch clause.
     * Allows recovering from errors by returning a valid RestyResult<T>.
     */
    public RestyPromise<T> recover(Function<RestyResult<T>, RestyResult<T>> handler) {
        RestyPromise<T> next = new RestyPromise<>();
        this.failureHandler = result -> {
            RestyResult<T> recovered = handler.apply(result);
            if (recovered.isSuccess()) {
                next.onSuccess(recovered.method, recovered.payload);
            } else {
                next.onFailure(recovered.method, recovered.exception);
            }
            return recovered;
        };
        this.successHandler = result -> {
            // propagate success unchanged
            next.onSuccess(result.method, result.payload);
            return result;
        };
        return next;
    }

    /**
     * Non-transforming catch clause.
     * Consumes the error without returning a new value.
     */
    public RestyPromise<T> catch_(Consumer<RestyResult<T>> handler) {
        RestyPromise<T> next = new RestyPromise<>();
        this.failureHandler = result -> {
            handler.accept(result);
            next.onFailure(result.method, result.exception);
            return result;
        };
        this.successHandler = result -> {
            next.onSuccess(result.method, result.payload);
            return result;
        };
        return next;
    }
}
