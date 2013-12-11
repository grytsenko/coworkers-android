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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

/**
 * Photo for contact.
 * 
 * <p>
 * We use additional data, because photos synchronized separately.
 */
public final class ContactPhoto {

    /**
     * The identifier of column which contains URL for photo.
     */
    public static final String URL = Data.DATA1;

    /**
     * The identifier of column which contains the status of photo. It is
     * boolean flag which is <code>true</code> if photo was synchronized and
     * <code>false</code> otherwise.
     */
    public static final String IS_SYNCED = Data.DATA2;

    private String url;
    private boolean synced;

    /**
     * Creates a photo.
     * 
     * @param url
     *            the URL of photo.
     * @param synced
     *            the status of photo.
     */
    public ContactPhoto(String url, boolean synced) {
        this.url = url;
        this.synced = synced;
    }

    /**
     * Gets the URL of photo.
     * 
     * @return the URL of photo.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the status of photo.
     * 
     * @return <code>true</code> if photo was synchronized and
     *         <code>false</code> otherwise.
     */
    public boolean isSynced() {
        return synced;
    }

    /**
     * Obtains data about photo for {@link ContentResolver}.
     * 
     * @return the set of values.
     */
    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(ContactPhoto.URL, url);
        values.put(ContactPhoto.IS_SYNCED, Boolean.toString(synced));
        return values;
    }

    /**
     * Updates URL of photo. If new URL differs from the current URL, then
     * status will be set to <code>false</code>.
     * 
     * @param newUrl
     *            the new URL (can be <code>null</code>).
     */
    public void update(String newUrl) {
        boolean urlDiffer = !TextUtils.equals(url, newUrl);

        url = newUrl;
        if (urlDiffer) {
            synced = false;
        }
    }

}
