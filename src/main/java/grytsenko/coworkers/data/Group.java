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

/**
 * Group for contacts.
 */
public final class Group {

    private String uid;
    private long id;

    /**
     * Creates a group.
     * 
     * @param uid
     *            the unique identifier of group.
     * @param id
     *            the identifier assigned by Android.
     */
    public Group(String uid, long id) {
        this.uid = uid;
        this.id = id;
    }

    /**
     * Returns the unique identifier of group.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the identifier assigned by Android.
     */
    public long getId() {
        return id;
    }

}
