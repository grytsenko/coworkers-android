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

import static java.text.MessageFormat.format;

import java.util.ArrayList;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Groups;
import android.util.Log;

/**
 * Provides access to groups of contacts.
 */
public class GroupsProvider extends DataProvider {

    private static final String TAG = GroupsProvider.class.getName();

    /**
     * Creates provider.
     * 
     * @param context
     *            the context to use.
     */
    public GroupsProvider(Context context) {
        super(context);
    }

    /**
     * Finds group.
     * 
     * @param account
     *            the current user account.
     * @param uid
     *            the identifier of group.
     * 
     * @return the found group or <code>null</code> if group not found.
     */
    public Group findByUid(Account account, final String uid) {
        String[] projection = new String[] { Groups._ID, Groups.TITLE };
        String selection = Groups.SYNC1 + "=? and " + Groups.ACCOUNT_NAME
                + "=? and " + Groups.ACCOUNT_TYPE + "=?";
        String[] selectionArgs = new String[] { uid, account.name, account.type };
        Cursor cursor = resolver.query(Groups.CONTENT_URI, projection,
                selection, selectionArgs, null);

        return readObject(cursor, new Reader<Group>() {
            @Override
            public Group read(Cursor cursor) {
                long id = readLong(cursor, Groups._ID);
                return new Group(uid, id);
            }
        });
    }

    /**
     * Creates group.
     * 
     * @param account
     *            the current user account.
     * @param uid
     *            the identifier of group.
     * @param title
     *            the title of group.
     * 
     * @return the created group.
     */
    public Group createGroup(Account account, String uid, String title) {
        Log.d(TAG, format("Create group {0}.", uid));

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        batch.add(ContentProviderOperation.newInsert(Groups.CONTENT_URI)
                .withValue(Groups.SYNC1, uid).withValue(Groups.TITLE, title)
                .withValue(Groups.ACCOUNT_NAME, account.name)
                .withValue(Groups.ACCOUNT_TYPE, account.type)
                .withValue(Groups.GROUP_VISIBLE, 1).build());

        long id = createObject(batch);
        return new Group(uid, id);
    }

    /**
     * Updates title of group.
     * 
     * @param group
     *            the group to update.
     * @param newTitle
     *            the new title.
     */
    public void updateTitle(Group group, String newTitle) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        Uri groupUri = ContentUris.withAppendedId(Groups.CONTENT_URI,
                group.getId());
        batch.add(ContentProviderOperation.newUpdate(groupUri)
                .withValue(Groups.TITLE, newTitle).build());

        updateObject(batch);
    }

}
