package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.helpers.ServerConnector;
import java.io.IOException;
import com.flatflatching.flatflatching.helpers.ServerConnector.Method;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rafael on 09.10.2015.
 *
 */
public final class RequestService {
    //All methods are Blocking: only use in Asynctask
    private RequestService() {

    }
    public static String sendRequest(final Method method, final String requestUrl) throws IOException {
        ServerConnector serverConnector = getServerConnector(method, requestUrl);
        return getResponse(serverConnector);

    }
    public static String sendRequestWithData(final Method method, final String requestUrl, final JSONObject data) throws IOException, JSONException {
        ServerConnector serverConnector = new ServerConnector(requestUrl, data, method);
        return getResponse(serverConnector);
    }

    private static ServerConnector getServerConnector(final Method method, final String requestUrl) throws IOException {
        return new ServerConnector(requestUrl,  method);
    }

    private static String getResponse(final ServerConnector serverConnector) throws IOException {
        return serverConnector.finish();

    }
}
