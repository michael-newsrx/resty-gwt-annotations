/*
 * Attribution: Generated for Michael (NewSRX Tech) by Microsoft Copilot
 * Context: ButterApiApplication (Spring Boot 3.x, MVC migration, JDBC session management)
 * Timestamp (EST5EDT): 2025-11-24 15:50:00
 */

package com.newsrx.restygwt.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.function.Consumer;
import java.util.function.Function;

public class RestyPromise<T> implements MethodCallback<T> {

    // Handler for this stage
    private Function<RestyResult<T>, ?> successHandler;
    private Function<RestyResult<T>, ?> failureHandler;

    // Explicit downstream link; used to forward results even if the current stage has no handler
    private RestyPromise<?> next;

    /**
     * Chainable transformable then clause.
     * Transforms RestyResult<T> into RestyResult<U>, forwarding to the next stage.
     */
    public <U> RestyPromise<U> map(Function<RestyResult<T>, RestyResult<U>> handler) {
        RestyPromise<U> nextStage = new RestyPromise<>();
        this.next = nextStage;

        this.successHandler = result -> {
            RestyResult<U> transformed = handler.apply(result);
            if (transformed.isSuccess()) {
                nextStage.onSuccess(transformed.method, transformed.payload);
            } else {
                nextStage.onFailure(transformed.method, transformed.exception);
            }
            return transformed;
        };

        this.failureHandler = result -> {
            // propagate failure downstream unchanged
            nextStage.onFailure(result.method, result.exception);
            return result;
        };

        return nextStage;
    }

    @Override
    public void onSuccess(Method method, T response) {
        RestyResult<T> result = new RestyResult<>(method, response, null, method.getResponse());
        if (successHandler != null) {
            successHandler.apply(result);
        } else if (next != null) {
            // No local success handler, but there is a downstream: forward as-is
            @SuppressWarnings("unchecked") RestyPromise<T> down = (RestyPromise<T>) next;
            down.onSuccess(result.method, result.payload);
        }
    }

    @Override
    public void onFailure(Method method, Throwable exception) {
        RestyResult<T> result =
                new RestyResult<>(method, null, exception, method != null ? method.getResponse() : null);
        if (failureHandler != null) {
            failureHandler.apply(result);
        } else if (next != null) {
            // No local failure handler, forward as-is
            @SuppressWarnings("unchecked") RestyPromise<T> down = (RestyPromise<T>) next;
            down.onFailure(result.method, result.exception);
        }
    }

    /**
     * Non-transforming then clause.
     * Consumes the result and forwards the same payload downstream.
     */
    public RestyPromise<T> then(Consumer<RestyResult<T>> handler) {
        RestyPromise<T> nextStage = new RestyPromise<>();
        this.next = nextStage;

        this.successHandler = result -> {
            handler.accept(result);
            nextStage.onSuccess(result.method, result.payload);
            return result;
        };

        this.failureHandler = result -> {
            nextStage.onFailure(result.method, result.exception);
            return result;
        };

        return nextStage;
    }

    /**
     * Chainable transformable catch clause.
     * Allows recovering from errors by returning a valid RestyResult<T>.
     */
    public RestyPromise<T> recover(Function<RestyResult<T>, RestyResult<T>> handler) {
        RestyPromise<T> nextStage = new RestyPromise<>();
        this.next = nextStage;

        this.failureHandler = result -> {
            RestyResult<T> recovered = handler.apply(result);
            if (recovered.isSuccess()) {
                nextStage.onSuccess(recovered.method, recovered.payload);
            } else {
                nextStage.onFailure(recovered.method, recovered.exception);
            }
            return recovered;
        };

        this.successHandler = result -> {
            // propagate success unchanged
            nextStage.onSuccess(result.method, result.payload);
            return result;
        };

        return nextStage;
    }

    /**
     * Non-transforming catch clause.
     * Consumes the error and forwards the same failure downstream.
     */
    public RestyPromise<T> catch_(Consumer<RestyResult<T>> handler) {
        RestyPromise<T> nextStage = new RestyPromise<>();
        this.next = nextStage;

        this.failureHandler = result -> {
            handler.accept(result);
            nextStage.onFailure(result.method, result.exception);
            return result;
        };

        this.successHandler = result -> {
            nextStage.onSuccess(result.method, result.payload);
            return result;
        };

        return nextStage;
    }
}
