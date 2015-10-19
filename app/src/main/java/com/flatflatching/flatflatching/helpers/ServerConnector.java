package com.flatflatching.flatflatching.helpers;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * 
 * @author www.codejava.net
 *
 */
public final class ServerConnector {

    public enum Method {
        GET,
        POST
    }

    private final String boundary;
    private static final int BUFFER_SIZE = 4096;
    private static final String LINE_FEED = "\r\n";
    private final HttpURLConnection httpConn;
    private final String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private Method method;

    /** 
     * This constructor initializes a new HTTP POST request with content type is
     * set to multipart/form-data.
     * 
     * @param requestUrl the requestendpoint
     * @param charset the charset used
     * @throws IOException
     */
    public ServerConnector(final String requestUrl, final String charset, final Method method)
            throws IOException {
        this.charset = charset;
        this.method = method;
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        final URL url = new URL(requestUrl);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoInput(true);
        switch (this.method) {
            case GET:
                httpConn.setDoOutput(false);
                break;
            case POST:
                httpConn.setDoOutput(true);
                httpConn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);
                httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
                outputStream = httpConn.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                        true);
                break;
        }
    }

    /**
     * Adds a form field to the request.
     * 
     * @param name
     *            field name
     * @param value
     *            field value
     */
    public void addFormField(final String name, final String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a form field to the request.
     * 
     * @param name
     *            field name
     * @param value
     *            field value
     * @param contentType the contenttype which is used
     */
    public void addFormField(final String name, final String value, final String contentType) { // NO_UCD (unused code)
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ").append(contentType).append("; charset=").append(charset)
                .append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     * 
     * @param fieldName
     *            name attribute in <input type="file" name="..." />
     * @param uploadFile
     *            a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(final String fieldName, final File uploadFile) // NO_UCD (unused code)
            throws IOException {
        final String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").
                append(fieldName).append("\"; filename=\"").append(fileName).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        final FileInputStream inputStream = new FileInputStream(uploadFile);
        final byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = inputStream.read(buffer);
        while (bytesRead != -1) {
            outputStream.write(buffer, 0, bytesRead);
            bytesRead = inputStream.read(buffer);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     * 
     * @param name
     *            - name of the header field
     * @param value
     *            - value of the header field
     */
    public void addHeaderField(final String name, final String value) { // NO_UCD (unused code)
        writer.append(name).append(": ").append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     * 
     * @return a list of Strings as response in case the server returned status
     *         OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException {
        final List<String> response = new ArrayList<>();
        if(this.method == Method.POST) {
            writer.append(LINE_FEED).flush();
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
            writer.close();
        }
        // checks server's status code first
        final int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                response.add(line);
                line = reader.readLine();
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }
}