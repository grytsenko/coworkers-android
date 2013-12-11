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

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

/**
 * Provides access to contacts.
 */
public class ContactsProvider extends DataProvider {

    /**
     * Creates provider.
     * 
     * @param context
     *            the context to use.
     */
    public ContactsProvider(Context context) {
        super(context);
    }

    /**
     * Finds all contacts in group.
     * 
     * @param group
     *            the group to search.
     * 
     * @return the found contacts.
     */
    public List<Contact> findByGroup(Group group) {
        String[] projection = new String[] { GroupMembership.RAW_CONTACT_ID };
        String selection = GroupMembership.GROUP_ROW_ID + "=? and "
                + GroupMembership.MIMETYPE + "=?";
        String[] selectionArgs = new String[] { Long.toString(group.getId()),
                GroupMembership.CONTENT_ITEM_TYPE };
        Cursor cursor = resolver.query(Data.CONTENT_URI, projection, selection,
                selectionArgs, null);

        return readObjects(cursor, new Reader<Contact>() {
            @Override
            public Contact read(Cursor cursor) {
                long id = readLong(cursor, GroupMembership.RAW_CONTACT_ID);
                return findById(id);
            }
        });
    }

    /**
     * Finds contact.
     * 
     * @param id
     *            the identifier of contact.
     * 
     * @return the found contact.
     */
    public Contact findById(final long id) {
        String[] projection = new String[] { RawContacts.SYNC1 };
        Uri uri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, id);
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        return readObject(cursor, new Reader<Contact>() {
            @Override
            public Contact read(Cursor cursor) {
                String uid = readString(cursor, RawContacts.SYNC1);
                return new Contact(uid, id);
            }
        });
    }

    /**
     * Creates contact.
     * 
     * @param account
     *            the current user account.
     * @param group
     *            the group for contact.
     * @param employee
     *            the data about employee.
     * @param preferNativeName
     *            indicates that name in native language is preferred.
     * 
     * @return the created contact.
     */
    public Contact createContact(Account account, Group group,
            Employee employee, boolean preferNativeName) {
        String uid = employee.getUid();

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        batch.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_NAME, account.name)
                .withValue(RawContacts.ACCOUNT_TYPE, account.type)
                .withValue(RawContacts.SYNC1, uid).build());

        batch.add(doInsert(StructuredName.CONTENT_ITEM_TYPE,
                employee.getFullName(preferNativeName)));
        batch.add(doInsert(Organization.CONTENT_ITEM_TYPE,
                employee.getPosition()));

        batch.add(doInsert(Email.CONTENT_ITEM_TYPE, employee.getEmail()));
        batch.add(doInsert(Phone.CONTENT_ITEM_TYPE, employee.getMobile()));
        batch.add(doInsert(Im.CONTENT_ITEM_TYPE, employee.getSkype()));

        ContactPhoto photo = new ContactPhoto(employee.getPhoto(), false);
        batch.add(doInsert(Photo.CONTENT_ITEM_TYPE, photo.getContent()));

        batch.add(doInsert(GroupMembership.CONTENT_ITEM_TYPE,
                GroupMembership.GROUP_ROW_ID, group.getId()));

        long id = createObject(batch);
        return new Contact(uid, id);
    }

    /**
     * Updates contact.
     * 
     * @param contact
     *            the contact to update.
     * @param employee
     *            the data about employee.
     * @param preferNativeName
     *            indicates that name in native language is preferred.
     */
    public void updateContact(Contact contact, Employee employee,
            boolean preferNativeName) {
        long id = contact.getId();

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        batch.add(doUpdate(id, StructuredName.CONTENT_ITEM_TYPE,
                employee.getFullName(preferNativeName)));
        batch.add(doUpdate(id, Organization.CONTENT_ITEM_TYPE,
                employee.getPosition()));

        batch.add(doUpdate(id, Email.CONTENT_ITEM_TYPE, employee.getEmail()));
        batch.add(doUpdate(id, Phone.CONTENT_ITEM_TYPE, employee.getMobile()));
        batch.add(doUpdate(id, Im.CONTENT_ITEM_TYPE, employee.getSkype()));

        ContactPhoto photo = findPhoto(contact);
        photo.update(employee.getPhoto());
        batch.add(doUpdate(id, Photo.CONTENT_ITEM_TYPE, photo.getContent()));

        updateObject(batch);
    }

    /**
     * Removes contact.
     * 
     * @param contact
     *            the contact to remove.
     */
    public void removeContact(Contact contact) {
        long id = contact.getId();

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        Uri contactUri = ContentUris
                .withAppendedId(RawContacts.CONTENT_URI, id)
                .buildUpon()
                .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,
                        "true").build();
        batch.add(ContentProviderOperation.newDelete(contactUri).build());

        updateObject(batch);
    }

    /**
     * Finds photo for contact.
     * 
     * @param contact
     *            the data about contact.
     * 
     * @return the found photo.
     */
    public ContactPhoto findPhoto(Contact contact) {
        String[] projection = new String[] { ContactPhoto.URL,
                ContactPhoto.IS_SYNCED };
        String selection = Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE
                + "=?";
        String[] selectionArgs = new String[] { Long.toString(contact.getId()),
                Photo.CONTENT_ITEM_TYPE };
        Cursor cursor = resolver.query(Data.CONTENT_URI, projection, selection,
                selectionArgs, null);

        return readObject(cursor, new Reader<ContactPhoto>() {
            @Override
            public ContactPhoto read(Cursor cursor) {
                String url = readString(cursor, ContactPhoto.URL);
                boolean synced = readBoolean(cursor, ContactPhoto.IS_SYNCED);
                return new ContactPhoto(url, synced);
            }
        });
    }

    /**
     * Updates photo.
     * 
     * @param contact
     *            the contact to update.
     * @param photo
     *            the new photo for contact (can be <code>null</code>).
     */
    public void updatePhoto(Contact contact, byte[] photo) {
        long id = contact.getId();

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        batch.add(doUpdate(id, Photo.CONTENT_ITEM_TYPE, Photo.PHOTO, photo));
        batch.add(doUpdate(id, Photo.CONTENT_ITEM_TYPE, ContactPhoto.IS_SYNCED,
                Boolean.toString(true)));

        updateObject(batch);
    }

    private Builder prepareInsert(String mime) {
        return ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValue(Data.MIMETYPE, mime)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0);
    }

    private <T> ContentProviderOperation doInsert(String mime, String key,
            T value) {
        return prepareInsert(mime).withValue(key, value).build();
    }

    private <T> ContentProviderOperation doInsert(String mime,
            ContentValues values) {
        return prepareInsert(mime).withValues(values).build();
    }

    private Builder prepareUpdate(long id, String mime) {
        String selection = Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE
                + "=?";
        String[] selectionArgs = new String[] { Long.toString(id), mime };
        Cursor cursor = resolver.query(Data.CONTENT_URI, null, selection,
                selectionArgs, null);

        try {
            if (cursor == null || !cursor.moveToFirst()) {
                return ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValue(Data.MIMETYPE, mime)
                        .withValue(Data.RAW_CONTACT_ID, id);
            }

            return ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                    .withSelection(selection, selectionArgs);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private <T> ContentProviderOperation doUpdate(long id, String mime,
            String key, T value) {
        return prepareUpdate(id, mime).withValue(key, value).build();
    }

    private ContentProviderOperation doUpdate(long id, String mime,
            ContentValues values) {
        return prepareUpdate(id, mime).withValues(values).build();
    }

}
