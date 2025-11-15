package com.newsrx.restygwt.util;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.HashMap;
import java.util.Map;

public class CallbackPromise<T> implements MethodCallback<T> {
    private final Promise<T> promise;
    private Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<T> resolve;
    private Promise.PromiseExecutorCallbackFn.RejectCallbackFn reject;

    public CallbackPromise() {
        Promise.PromiseExecutorCallbackFn<T> callbackFn = (resolve, reject) -> {
            CallbackPromise.this.resolve = resolve;
            CallbackPromise.this.reject = reject;
        };
        promise = new Promise<T>(callbackFn);
    }

    @Override
    public void onFailure(Method method, Throwable exception) {
        DomGlobal.console.log("===== XHR onFailure =====", method, exception);
        if (method == null) {
            reject.onInvoke(exception);
            return;
        }
        Response response = method.getResponse();
        if (response == null) {
            reject.onInvoke(exception);
            return;
        }
        String text = response.getText();
        if (text == null || text.isBlank()) {
            reject.onInvoke(exception);
            return;
        }
        Header[] headers = response.getHeaders();
        Map<String, String> headerMap = new HashMap<>();
        for (Header header : headers) {
            headerMap.put(header.getName(), header.getValue());
        }
        int statusCode = response.getStatusCode();
        String statusText = response.getStatusText();
        reject.onInvoke(new CallbackRuntimeException("", exception, text, headerMap, statusCode, statusText));
    }

    @Override
    public void onSuccess(Method method, T response) {
        resolve.onInvoke(response);
    }

    public Promise<T> getPromise() {
        return promise;
    }

}
