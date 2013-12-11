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
package grytsenko.coworkers.sync;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import grytsenko.coworkers.R;
import grytsenko.coworkers.data.Contact;
import grytsenko.coworkers.data.ContactPhoto;
import grytsenko.coworkers.data.ContactsProvider;
import grytsenko.coworkers.data.DataException;
import grytsenko.coworkers.data.Group;
import grytsenko.coworkers.data.GroupsProvider;
import grytsenko.coworkers.util.GeneralSettings;
import grytsenko.coworkers.util.NetworkConnectivity;
import grytsenko.coworkers.util.SyncScheduler;
import grytsenko.coworkers.web.Employee;
import grytsenko.coworkers.web.WebClient;
import grytsenko.coworkers.web.WebException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.content.SyncStats;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * Synchronizes contacts of employees.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getName();

    private WebClient webClient;

    private GroupsProvider groupsProvider;
    private ContactsProvider contactsProvider;

    private GeneralSettings settings;
    private NetworkConnectivity connectivity;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        webClient = new WebClient();

        groupsProvider = new GroupsProvider(context);
        contactsProvider = new ContactsProvider(context);

        settings = new GeneralSettings(context);
        connectivity = new NetworkConnectivity(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        try {
            Group group = syncGroup(account, syncResult);
            Log.d(TAG, "Group synced.");

            checkCancelled();

            Map<String, Employee> employees = getCoworkers(account, syncResult);
            Log.d(TAG, format("Found %d employees.", employees.size()));

            checkCancelled();

            Map<String, Contact> contacts = syncContacts(account, group,
                    employees, syncResult);

            syncPhotos(contacts);

            SyncScheduler.scheduleNext(account, settings.syncFrequency());

            if (settings.notificationsEnabled()) {
                StatusService.notify(getContext(), R.string.sync_completed);
            }

            Log.d(TAG, "Sync completed.");
        } catch (SyncException exception) {
            Log.d(TAG, "Sync interrupted.", exception);
        } catch (Exception exception) {
            Log.e(TAG, "Sync failed.", exception);
        }
    }

    private Group syncGroup(Account account, SyncResult results) {
        String uid = getContext().getString(R.string.group_uid);
        String title = settings.groupTitle();

        try {
            Group group = groupsProvider.findByUid(account, uid);

            if (group == null) {
                return groupsProvider.createGroup(account, uid, title);
            }

            groupsProvider.updateTitle(group, title);
            return group;
        } catch (DataException exception) {
            results.databaseError = true;
            throw new SyncException("Group not synced.", exception);
        }
    }

    private Map<String, Employee> getCoworkers(Account account,
            SyncResult results) {
        String username = account.name;
        AccountManager accountManager = AccountManager.get(getContext());
        String password = accountManager.getPassword(account);

        try {
            String serviceUrl = getContext().getString(R.string.rest_coworkers);
            List<Employee> employees = webClient.getEmployees(serviceUrl,
                    username, password);
            return Employee.mapByUid(employees);
        } catch (WebException exception) {
            results.tooManyRetries = true;
            throw new SyncException("Server not available.", exception);
        }
    }

    private Map<String, Contact> syncContacts(Account account, Group group,
            Map<String, Employee> employees, SyncResult results) {
        Map<String, Contact> contacts = Contact.mapByUid(contactsProvider
                .findByGroup(group));
        Log.d(TAG, format("Found %d contacts.", contacts.size()));

        SyncStats stats = results.stats;

        List<Contact> createdContacts = createContacts(account, group,
                employees, contacts, stats);
        Log.d(TAG, format("Created %d contacts.", stats.numInserts));

        updateContacts(employees, contacts, stats);
        Log.d(TAG, format("Updated %d contacts.", stats.numUpdates));

        List<String> removedContacts = removeContacts(employees, contacts,
                stats);
        Log.d(TAG, format("Removed %d contacts.", stats.numDeletes));

        Log.d(TAG, format("Skipped %d contacts.", stats.numSkippedEntries));

        for (Contact contact : createdContacts) {
            contacts.put(contact.getUid(), contact);
        }
        for (String uid : removedContacts) {
            contacts.remove(uid);
        }

        return contacts;
    }

    private List<Contact> createContacts(Account account, Group group,
            Map<String, Employee> employees, Map<String, Contact> contacts,
            SyncStats stats) {
        List<Contact> createdContacts = new ArrayList<Contact>();

        boolean preferNativeName = settings.preferNativeNames();

        for (Employee employee : employees.values()) {
            String uid = employee.getUid();
            if (contacts.containsKey(uid)) {
                continue;
            }

            checkCancelled();

            try {
                Log.d(TAG, format("Create contact for %s.", uid));
                Contact contact = contactsProvider.createContact(account,
                        group, employee, preferNativeName);
                createdContacts.add(contact);
                ++stats.numInserts;
            } catch (DataException exception) {
                Log.w(TAG, "Not created.", exception);
                ++stats.numSkippedEntries;
            }
        }

        return createdContacts;
    }

    /**
     * Updates existing contacts.
     */
    private void updateContacts(Map<String, Employee> employees,
            Map<String, Contact> contacts, SyncStats stats) {
        boolean preferNativeName = settings.preferNativeNames();

        for (Employee employee : employees.values()) {
            String uid = employee.getUid();
            if (!contacts.containsKey(uid)) {
                continue;
            }
            Contact contact = contacts.get(uid);

            checkCancelled();

            try {
                Log.d(TAG, format("Update contact for %s.", uid));
                contactsProvider.updateContact(contact, employee,
                        preferNativeName);
                ++stats.numUpdates;
            } catch (DataException exception) {
                Log.w(TAG, "Not updated.", exception);
                ++stats.numSkippedEntries;
            }
        }
    }

    /**
     * Removes obsolete contacts.
     */
    private List<String> removeContacts(Map<String, Employee> employees,
            Map<String, Contact> contacts, SyncStats stats) {
        List<String> removedContacts = new ArrayList<String>();

        for (Contact contact : contacts.values()) {
            String uid = contact.getUid();
            if (employees.containsKey(uid)) {
                continue;
            }

            checkCancelled();

            try {
                Log.d(TAG, format("Remove contact for %s.", uid));
                contactsProvider.removeContact(contact);
                removedContacts.add(uid);
                ++stats.numDeletes;
            } catch (DataException exception) {
                Log.w(TAG, "Not removed.", exception);
                ++stats.numSkippedEntries;
            }
        }

        return removedContacts;
    }

    private void syncPhotos(Map<String, Contact> contacts) {
        if (!settings.syncPhotosEnabled()) {
            Log.d(TAG, "Sync of photos disabled.");
            return;
        }

        for (Contact contact : contacts.values()) {
            checkCancelled();

            try {
                Log.d(TAG, format("Sync photo for %s.", contact.getUid()));

                syncPhoto(contact);
            } catch (DataException exception) {
                Log.w(TAG, "Not synced.", exception);
            }
        }
    }

    private void syncPhoto(Contact contact) {
        if (!connectivity.isSuitable(settings.syncPhotosOverWifiOnly())) {
            return;
        }

        ContactPhoto photo = contactsProvider.findPhoto(contact);

        if (photo.isSynced()) {
            Log.d(TAG, "Photo is up to date.");
            return;
        }

        if (TextUtils.isEmpty(photo.getUrl())) {
            Log.d(TAG, "Remove photo.");
            contactsProvider.updatePhoto(contact, null);
            return;
        }

        try {
            Log.d(TAG, "Update photo.");
            byte[] image = webClient.getPhoto(photo.getUrl());
            contactsProvider.updatePhoto(contact, image);
        } catch (WebException exception) {
            throw new DataException("Photo not available.", exception);
        }
    }

    /**
     * Checks, that synchronization was cancelled.
     */
    private void checkCancelled() {
        boolean cancelled = currentThread().isInterrupted();
        if (cancelled) {
            throw new SyncException("Sync was cancelled.");
        }
    }

}
