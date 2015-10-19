package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.helpers.ServerConnector;
import java.io.IOException;
import java.util.List;

import com.flatflatching.flatflatching.helpers.ServerConnector.Method;
/**
 * Created by rafael on 09.10.2015.
 *
 */
public final class RequestService {
    //All methods are Blocking: only use in Asynctask
    private static final String CHAR_SET = "UTF-8";

    private RequestService() {

    }
    public static String sendRequest(Method method, String requestUrl) throws IOException {
        ServerConnector serverConnector = getServerConnector(method, requestUrl);
        return getResponse(serverConnector);

    }
    public static String sendRequestWithData(Method method, String requestUrl, String data) throws IOException {
        ServerConnector serverConnector = new ServerConnector(requestUrl, data);
        return getResponse(serverConnector);
    }

    private static ServerConnector getServerConnector(Method method, String requestUrl) throws IOException {
        return new ServerConnector(requestUrl,  method);
    }

    private static String getResponse(ServerConnector serverConnector) throws IOException {
        return serverConnector.finish();

    }
}
