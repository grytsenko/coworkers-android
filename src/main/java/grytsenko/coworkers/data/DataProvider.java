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

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Provides access to data.
 */
public abstract class DataProvider {

    private static final String TAG = DataProvider.class.getName();

    /**
     * Transforms data from cursor into object.
     * 
     * @param <T>
     *            the type of object.
     */
    protected interface Reader<T> {

        /**
         * Reads object from current position of cursor.
         * 
         * @param cursor
         *            the cursor to be read.
         * 
         * @return the read object.
         * 
         * @throws DataException
         *             if object could not be read.
         */
        T read(Cursor cursor);

    }

    /**
     * Reads first object from cursor, if possible.
     * 
     * @param cursor
     *            the cursor to be read.
     * @param reader
     *            the reader for object.
     * 
     * @return the read object.
     * 
     * @throws DataException
     *             if object could not be read.
     */
    protected static <T> T readObject(Cursor cursor, Reader<T> reader) {
        if (cursor == null) {
            return null;
        }

        try {
            if (!cursor.moveToFirst()) {
                return null;
            }

            return reader.read(cursor);
        } finally {
            cursor.close();
        }
    }

    /**
     * Reads all objects from cursor.
     * 
     * <p>
     * Ignores all objects, that could not be read.
     * 
     * @param cursor
     *            the cursor to be read.
     * @param reader
     *            the reader for object.
     * 
     * @return the read objects.
     */
    protected static <T> List<T> readObjects(Cursor cursor, Reader<T> reader) {
        List<T> objects = new ArrayList<T>();
        if (cursor == null) {
            return objects;
        }

        try {
            if (!cursor.moveToFirst()) {
                return objects;
            }

            int numSkipped = 0;
            do {
                try {
                    objects.add(reader.read(cursor));
                } catch (DataException exception) {
                    Log.d(TAG, "Inconsistent data.", exception);
                    ++numSkipped;
                }
            } while (cursor.moveToNext());
            Log.d(TAG,
                    format("Read %d, skipped %d.", objects.size(), numSkipped));
        } finally {
            cursor.close();
        }

        return objects;
    }

    /**
     * Reads string value.
     * 
     * @param cursor
     *            the cursor to be read.
     * @param column
     *            the name of column which contains value.
     * 
     * @return the value of column.
     */
    protected static String readString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }

    /**
     * Reads long integer value.
     * 
     * @param cursor
     *            the cursor to be read.
     * @param column
     *            the name of column which contains value.
     * 
     * @return the value of column.
     */
    protected static long readLong(Cursor cursor, String column) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(column));
    }

    /**
     * Reads string value and then converts it to boolean value.
     * 
     * @param cursor
     *            the cursor to be read.
     * @param column
     *            the name of column which contains value.
     * 
     * @return the value of column.
     */
    protected static boolean readBoolean(Cursor cursor, String column) {
        return Boolean.parseBoolean(readString(cursor, column));
    }

    protected final ContentResolver resolver;

    /**
     * Creates provider.
     * 
     * @param context
     *            the context to use.
     */
    protected DataProvider(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Invalid context.");
        }

        resolver = context.getContentResolver();
    }

    /**
     * Executes batch of operations to create new object.
     * 
     * <p>
     * Assumes that the first operation returns identifier of created object.
     * 
     * @param batch
     *            the operations to execute.
     * 
     * @return the identifier of created object.
     */
    protected long createObject(ArrayList<ContentProviderOperation> batch) {
        try {
            ContentProviderResult[] results = resolver.applyBatch(
                    ContactsContract.AUTHORITY, batch);
            return ContentUris.parseId(results[0].uri);
        } catch (Exception exception) {
            throw new DataException("Object not created.", exception);
        }
    }

    /**
     * Executes batch of operations to update or remove existing object.
     * 
     * @param batch
     *            the operations to execute.
     */
    protected void updateObject(ArrayList<ContentProviderOperation> batch) {
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, batch);
        } catch (Exception exception) {
            throw new DataException("Object not updated.", exception);
        }
    }

}
