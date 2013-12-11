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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.text.TextUtils;

/**
 * Information about employee, that is provided by REST service.
 */
public final class Employee {

    private static final String JSON_UID = "uid";

    private static final String JSON_FIRST_NAME = "firstName";
    private static final String JSON_LAST_NAME = "lastName";
    private static final String JSON_LAST_NAME_NATIVE = "lastNameNative";
    private static final String JSON_FIRST_NAME_NATIVE = "firstNameNative";

    private static final String JSON_POSITION = "position";

    private static final String JSON_EMAIL = "email";
    private static final String JSON_MOBILE = "mobile";
    private static final String JSON_SKYPE = "skype";

    private static final String JSON_PHOTO = "photo";

    /**
     * Creates employee from JSON object.
     * 
     * @param json
     *            the JSON object to parse.
     * 
     * @return the created employee.
     * 
     * @throws JSONException
     *             the data has invalid format.
     */
    public static Employee fromJson(JSONObject json) throws JSONException {
        Employee employee = new Employee();

        employee.uid = getMandatoryField(json, JSON_UID);

        employee.firstName = getMandatoryField(json, JSON_FIRST_NAME);
        employee.lastName = getMandatoryField(json, JSON_LAST_NAME);

        if (!json.isNull(JSON_FIRST_NAME_NATIVE)
                && !json.isNull(JSON_LAST_NAME_NATIVE)) {
            employee.firstNameNative = getMandatoryField(json,
                    JSON_FIRST_NAME_NATIVE);
            employee.lastNameNative = getMandatoryField(json,
                    JSON_LAST_NAME_NATIVE);
        }

        employee.position = getOptionalField(json, JSON_POSITION);

        employee.email = getOptionalField(json, JSON_EMAIL);
        employee.mobile = getOptionalField(json, JSON_MOBILE);
        employee.skype = getOptionalField(json, JSON_SKYPE);

        employee.photo = getOptionalField(json, JSON_PHOTO);

        return employee;
    }

    private static String getMandatoryField(JSONObject json, String name)
            throws JSONException {
        if (json.isNull(name)) {
            throw new JSONException(format("Field %s is mandatory.", name));
        }
        return json.getString(name);
    }

    private static String getOptionalField(JSONObject json, String name)
            throws JSONException {
        return json.isNull(name) ? null : json.getString(name);
    }

    /**
     * Maps employees by unique identifier.
     * 
     * @param employees
     *            the list of employees.
     * 
     * @return the set of employees.
     */
    public static Map<String, Employee> mapByUid(List<Employee> employees) {
        Map<String, Employee> byUid = new HashMap<String, Employee>();
        for (Employee employee : employees) {
            byUid.put(employee.uid, employee);
        }
        return byUid;
    }

    private String uid;

    private String firstName;
    private String lastName;
    private String firstNameNative;
    private String lastNameNative;

    private String position;

    private String mobile;
    private String email;
    private String skype;

    private String photo;

    private Employee() {
    }

    /**
     * Gets the unique identifier, that is used to distinguish employees.
     * 
     * @return the unique identifier.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns data about full name for {@link ContentResolver}.
     * 
     * @param preferNative
     *            shows that name in native language is preferred.
     * 
     * @return the set of values.
     */
    public ContentValues getFullName(boolean preferNative) {
        if (preferNative && hasNative()) {
            ContentValues values = new ContentValues();
            values.put(StructuredName.GIVEN_NAME, firstNameNative);
            values.put(StructuredName.FAMILY_NAME, lastNameNative);
            return values;
        }

        ContentValues values = new ContentValues();
        values.put(StructuredName.GIVEN_NAME, firstName);
        values.put(StructuredName.FAMILY_NAME, lastName);
        return values;
    }

    private boolean hasNative() {
        return !TextUtils.isEmpty(firstNameNative)
                && !TextUtils.isEmpty(lastNameNative);
    }

    /**
     * Obtains data about position for {@link ContentResolver}.
     * 
     * @return the set of values.
     */
    public ContentValues getPosition() {
        ContentValues values = new ContentValues();
        values.put(Organization.TITLE, position);
        values.put(Organization.TYPE, Organization.TYPE_WORK);
        return values;
    }

    /**
     * Obtains data about email address for {@link ContentResolver}.
     * 
     * @return the set of values.
     */
    public ContentValues getEmail() {
        ContentValues values = new ContentValues();
        values.put(Email.DATA, email);
        values.put(Email.TYPE, Email.TYPE_WORK);
        return values;
    }

    /**
     * Obtains data about mobile phone number for {@link ContentResolver}.
     * 
     * @return the set of values.
     */
    public ContentValues getMobile() {
        ContentValues values = new ContentValues();
        values.put(Phone.NUMBER, mobile);
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        return values;
    }

    /**
     * Obtains data about Skype name for {@link ContentResolver}.
     * 
     * @return the set of values.
     */
    public ContentValues getSkype() {
        ContentValues values = new ContentValues();
        values.put(Im.DATA, skype);
        values.put(Im.PROTOCOL, Im.PROTOCOL_SKYPE);
        values.put(Im.TYPE, Im.TYPE_OTHER);
        return values;
    }

    /**
     * Returns the URL of photo.
     */
    public String getPhoto() {
        return photo;
    }

}
