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
package grytsenko.coworkers.data;

import grytsenko.coworkers.web.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contact for employee.
 */
public final class Contact {

    /**
     * Maps contacts by unique identifier of employee.
     * 
     * @param contacts
     *            the list of contacts.
     * 
     * @return the mapped set of contacts.
     */
    public static Map<String, Contact> mapByUid(List<Contact> contacts) {
        Map<String, Contact> byUid = new HashMap<String, Contact>();
        for (Contact contact : contacts) {
            byUid.put(contact.uid, contact);
        }
        return byUid;
    }

    private String uid;
    private long id;

    /**
     * Creates a contact.
     * 
     * @param uid
     *            the unique identifier of employee.
     * @param id
     *            the identifier assigned by Android.
     */
    public Contact(String uid, long id) {
        this.uid = uid;
        this.id = id;
    }

    /**
     * Returns the unique identifier of employee.
     * 
     * @see Employee#getUid()
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the identifier of contact assigned by Android.
     */
    public long getId() {
        return id;
    }

}
