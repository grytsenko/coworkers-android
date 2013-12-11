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
package grytsenko.coworkers.util;

import grytsenko.coworkers.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * General settings for application.
 */
public class GeneralSettings {

    /**
     * Sets the default settings.
     * 
     * @param context
     *            the context to use.
     */
    public static void setDefault(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.sync_settings, false);
    }

    private Context context;
    private SharedPreferences preferences;

    /**
     * Creates general settings.
     * 
     * @param context
     *            the context to use.
     */
    public GeneralSettings(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Returns the group title.
     * 
     * @return the group title.
     */
    public String groupTitle() {
        return preferences.getString(context.getString(R.string.group_title),
                context.getString(R.string.group_title_default));
    }

    /**
     * Checks that sync of photos is enabled.
     * 
     * @return <code>true</code> if sync of photos is enabled.
     */
    public boolean syncPhotosEnabled() {
        return !TextUtils.equals(syncPhotos(),
                context.getString(R.string.sync_photos_none));
    }

    /**
     * Checks that sync of photos is allowed only over Wi-Fi.
     * 
     * @return <code>true</code> if sync is allowed only over Wi-Fi and
     *         <code>false</code> if sync is allowed over Wi-Fi or mobile
     *         network.
     */
    public boolean syncPhotosOverWifiOnly() {
        return TextUtils.equals(syncPhotos(),
                context.getString(R.string.sync_photos_wifi_only));
    }

    /**
     * Returns the selected option for sync of photos.
     * 
     * @return the selected value.
     */
    private String syncPhotos() {
        return preferences.getString(context.getString(R.string.sync_photos),
                context.getString(R.string.sync_photos_none));
    }

    /**
     * Checks that user prefers names in their native language.
     * 
     * @return <code>true</code> if user prefers names in their native language
     *         and <code>false</code> if user prefers names in English.
     */
    public boolean preferNativeNames() {
        return preferences.getBoolean(context.getString(R.string.native_names),
                false);
    }

    /**
     * Returns the frequency of sync.
     * 
     * @return the frequency of sync.
     */
    public SyncFrequency syncFrequency() {
        String weekly = context.getString(R.string.sync_frequency_weekly);
        String monthly = context.getString(R.string.sync_frequency_monthly);

        String value = preferences.getString(
                context.getString(R.string.sync_frequency), weekly);

        if (TextUtils.equals(value, weekly)) {
            return SyncFrequency.WEEKLY;
        } else if (TextUtils.equals(value, monthly)) {
            return SyncFrequency.MONTHLY;
        } else {
            throw new IllegalStateException("Invalid frequency.");
        }
    }

    /**
     * Checks that notifications are enabled.
     * 
     * @return <code>true</code> if notifications enabled and <code>false</code>
     *         otherwise.
     */
    public boolean notificationsEnabled() {
        return preferences.getBoolean(
                context.getString(R.string.show_notifications), false);
    }

}
