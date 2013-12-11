/**
 * Copyright (C) 2013 Anton Grytsenko (anthony.grytsenko@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grytsenko.coworkers.web;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Base64;
import android.util.Log;

/**
 * Performs download of data through HTTP.
 * 
 * <p>
 * This class is used by {@link WebClient}.
 */
class HttpClient {

    private static final String TAG = HttpClient.class.getName();

    private static final String AUTHORIZATION = "Authorization";

    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * Creates a client.
     */
    public HttpClient() {
    }

    /**
     * Downloads the specified resource.
     * 
     * @param url
     *            the URL of resource.
     * 
     * @return the loaded data.
     * 
     * @throws WebException
     *             if resource not available.
     */
    public byte[] download(String url) {
        return download(url, null);
    }

    /**
     * Downloads the specified resource with restricted access. Uses basic
     * authentication to access resource.
     * 
     * @param url
     *            the URL of resource.
     * @param username
     *            the username to access.
     * @param password
     *            the password to access.
     * 
     * @return the loaded data.
     * 
     * @throws WebException
     *             if resource not available or user not authorized.
     */
    public byte[] download(String url, String username, String password) {
        return download(url, encodeCredentials(username, password));
    }

    private byte[] download(String url, String authorization) {
        Log.d(TAG, format("Download %s.", url));
        try {
            URL validUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) validUrl
                    .openConnection();

            if (authorization != null) {
                connection.setRequestProperty(AUTHORIZATION, authorization);
            }

            try {
                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    throw new WebException("Invalid status.");
                }

                InputStream input = connection.getInputStream();
                return readAll(input);
            } finally {
                connection.disconnect();
            }
        } catch (IOException exception) {
            throw new WebException("Not available.", exception);
        }
    }

    private static String encodeCredentials(String username, String password) {
        byte[] credentials = (username + ":" + password).getBytes();
        byte[] encodedData = Base64.encode(credentials, Base64.NO_WRAP);
        return "Basic " + new String(encodedData);
    }

    private static byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }

        return output.toByteArray();
    }

}
