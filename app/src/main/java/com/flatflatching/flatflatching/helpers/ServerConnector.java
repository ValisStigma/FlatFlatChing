package com.flatflatching.flatflatching.helpers;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;

public final class ServerConnector {
    public enum Method {
        GET,
        POST
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Request request;

    public ServerConnector(final String requestUrl, Method method)
            throws IOException {
        client = new OkHttpClient();
        if(method == Method.GET)  {
            request = new Request.Builder()
                    .url(requestUrl)
                    .build();
        } else {
            request = new Request.Builder().url(requestUrl).build();
        }
    }

    public ServerConnector(final String requestUrl,final String requestData) {
        client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, requestData);

        request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();
    }

    public String finish() throws IOException {
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}