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
package grytsenko.coworkers.ui;

import grytsenko.coworkers.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Provides access to settings.
 * 
 * <p>
 * We use activity instead of fragment to provide backward compatibility.
 */
public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.sync_settings);

        EditTextPreference groupTitle = (EditTextPreference) findPreference(getString(R.string.group_title));
        groupTitle
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference,
                            Object newValue) {
                        return !TextUtils.isEmpty((String) newValue);
                    }

                });

        setValueAsSummary(getString(R.string.group_title));
        setValueAsSummary(getString(R.string.sync_frequency));
        setValueAsSummary(getString(R.string.sync_photos));

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        setValueAsSummary(key);
    }

    /**
     * Takes the value of preference and set it into summary.
     * 
     * @param key
     *            the key of preference.
     */
    private void setValueAsSummary(String key) {
        @SuppressWarnings("deprecation")
        Preference preference = findPreference(key);

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
        } else if (preference instanceof EditTextPreference) {
            EditTextPreference textPreference = (EditTextPreference) preference;
            textPreference.setSummary(textPreference.getText());
        }
    }

}
