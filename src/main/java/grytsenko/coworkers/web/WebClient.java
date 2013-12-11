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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class provides access to web resources.
 */
public final class WebClient {

    private static final String TAG = WebClient.class.getName();

    private static final int RETRIES_MAX = 3;
    private static final int PHOTO_QUALITY = 100;

    private HttpClient httpClient;

    /**
     * Creates a client.
     */
    public WebClient() {
        httpClient = new HttpClient();
    }

    /**
     * Gets list of employees from REST service. Expected that this service
     * returns data in JSON format.
     * 
     * @param url
     *            the URL of REST service.
     * @param username
     *            the username to access.
     * @param password
     *            the password to access.
     * 
     * @return the list of employees.
     * 
     * @throws WebException
     *             if data is not available or it has invalid format.
     */
    public List<Employee> getEmployees(String url, String username,
            String password) {
        Log.d(TAG, format("Get employees from %s.", url));

        byte[] data = downloadWithRetries(url, username, password);

        try {
            return toEmployees(new String(data));
        } catch (JSONException exception) {
            throw new WebException("Invalid JSON.", exception);
        }
    }

    private byte[] downloadWithRetries(String url, String username,
            String password) {
        for (int i = 0; i < RETRIES_MAX; ++i) {
            try {
                return httpClient.download(url, username, password);
            } catch (WebException exception) {
                Log.d(TAG, "Service not available.", exception);
            }
        }
        throw new WebException("Too many retries.");
    }

    private static List<Employee> toEmployees(String json) throws JSONException {
        List<Employee> employees = new ArrayList<Employee>();

        int numSkipped = 0;
        JSONArray employeesJson = new JSONArray(json);
        for (int i = 0; i < employeesJson.length(); ++i) {
            JSONObject employeeJson = employeesJson.getJSONObject(i);
            try {
                employees.add(Employee.fromJson(employeeJson));
            } catch (JSONException exception) {
                Log.d(TAG, "Inconsistent data.", exception);
                ++numSkipped;
            }
        }
        Log.d(TAG,
                format("Parsed %d, skipped %d.", employees.size(), numSkipped));

        return employees;
    }

    /**
     * Gets photo of employee.
     * 
     * @param url
     *            the URL of photo.
     * 
     * @return the photo in PNG format.
     * 
     * @throws WebException
     *             if photo is not available or it has invalid format.
     */
    public byte[] getPhoto(String url) {
        Log.d(TAG, format("Get photo from %s.", url));

        byte[] data = httpClient.download(url);

        try {
            return toPng(data);
        } catch (IOException exception) {
            throw new WebException("Invalid image.", exception);
        }
    }

    public static byte[] toPng(byte[] raw) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length);
        if (bitmap == null) {
            throw new IOException("Not decoded.");
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean compressed = bitmap.compress(CompressFormat.PNG, PHOTO_QUALITY,
                output);
        if (!compressed) {
            throw new IOException("Not compressed.");
        }

        return output.toByteArray();
    }

}
