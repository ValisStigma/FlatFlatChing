package com.flatflatching.flatflatching.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael on 04.10.2015.
 */
public class BaseRequest {

    String requestUrl;
    HttpURLConnection httpConn;
    URL url;
    public BaseRequest(final String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public BaseRequest openRequest() throws IOException {
        url = new URL(requestUrl);
        httpConn = (HttpURLConnection) url.openConnection();
        return this;
    }
    public String finish() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpConn.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }
        reader.close();
        httpConn.disconnect();
        return stringBuilder.toString();
    }
}
