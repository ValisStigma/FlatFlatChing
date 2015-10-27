package com.flatflatching.flatflatching.helpers;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Iterator;

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
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        if(method == Method.GET) {
            request = new Request.Builder()
                    .url(requestUrl)
                    .build();
        } else {
            request = new Request.Builder().url(requestUrl).build();
        }
    }

    public ServerConnector(final String requestUrl,final JSONObject jsonObject, Method method) throws JSONException {
        client = new OkHttpClient();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        switch (method) {
            case GET:
                Iterator<?> keys = jsonObject.keys();
                String queryString = "?";
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    String value = jsonObject.getString(key);
                    queryString = queryString + key + "=" + value + "&";
                }
                queryString = queryString.substring(0, queryString.length() - 1);

                request = new Request.Builder()
                        .url(requestUrl + queryString)
                        .build();
                break;
            case POST:
                request = new Request.Builder()
                        .url(requestUrl)
                        .post(body)
                        .build();
                break;
            default:
                request = new Request.Builder()
                        .url(requestUrl)
                        .post(body)
                        .build();
        }

    }

    public String finish() throws IOException {
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}