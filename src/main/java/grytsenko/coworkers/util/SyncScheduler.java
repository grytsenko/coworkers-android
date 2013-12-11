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

import static java.lang.String.format;

import java.util.Calendar;
import java.util.Date;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Performs scheduling of sync.
 */
public final class SyncScheduler {

    private static final String TAG = SyncScheduler.class.getName();

    /**
     * Performs scheduling of next sync.
     * 
     * @param account
     *            the account.
     * @param frequency
     *            the frequency of sync.
     */
    public static void scheduleNext(Account account, SyncFrequency frequency) {
        Date now = new Date();
        Date next = getNext(frequency);

        long offset = next.getTime() - now.getTime();
        ContentResolver.addPeriodicSync(account, ContactsContract.AUTHORITY,
                new Bundle(), offset);

        Log.d(TAG, format("Sync scheduled for %tF %<tT", next));
    }

    private static Date getNext(SyncFrequency frequency) {
        switch (frequency) {
        case MONTHLY:
            return nextMonth();
        case WEEKLY:
            return nextWeek();
        default:
            throw new IllegalArgumentException("Invalid frequency.");
        }
    }

    private static Date nextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return nextWeek(calendar.getTime());
    }

    private static Date nextWeek() {
        return nextWeek(new Date());
    }

    private static Date nextWeek(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        int days = calendar.getFirstDayOfWeek()
                - calendar.get(Calendar.DAY_OF_WEEK);
        if (days <= 0) {
            days += 7;
        }
        calendar.add(Calendar.DAY_OF_MONTH, days);

        setTime(calendar);

        return calendar.getTime();
    }

    private static void setTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    private SyncScheduler() {
    }

}
