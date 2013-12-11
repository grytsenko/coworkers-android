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

import grytsenko.coworkers.R;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Builder;

/**
 * Allows to notify user about something.
 * 
 * <p>
 * Only the latest notification will be shown to user. It means, each new
 * notification replaces previous.
 */
public class StatusService extends IntentService {

    private static final String SERVICE_NAME = StatusService.class.getName();

    private static final String SHOW_NOTIFICATION = "SHOW";
    private static final String HIDE_NOTIFICATION = "HIDE";

    private static final int NOTIFICATION_ID = 0;
    private static final String MESSAGE = "MESSAGE";

    /**
     * Shows notification with fixed message.
     * 
     * @param context
     *            the context to use.
     * @param messageId
     *            the identifier of string resource.
     */
    public static void notify(Context context, int messageId) {
        notify(context, context.getString(messageId));
    }

    /**
     * Shows notification with custom message.
     * 
     * @param context
     *            the context to use.
     * @param message
     *            the message to show.
     */
    public static void notify(Context context, String message) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(SHOW_NOTIFICATION);
        intent.putExtra(MESSAGE, message);
        context.startService(intent);
    }

    /**
     * Creates service.
     */
    public StatusService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (SHOW_NOTIFICATION.equals(action)) {
            String message = intent.getStringExtra(MESSAGE);
            showNotification(message);
        } else if (HIDE_NOTIFICATION.equals(action)) {
            hideNotification();
        }
    }

    private void showNotification(String message) {
        Builder builder = new Builder(this);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_main);

        Intent cancel = new Intent(this, StatusService.class);
        cancel.setAction(HIDE_NOTIFICATION);
        PendingIntent pending = PendingIntent.getService(this, 0, cancel, 0);
        builder.setContentIntent(pending);

        getManager().notify(NOTIFICATION_ID, builder.getNotification());
    }

    private void hideNotification() {
        getManager().cancel(NOTIFICATION_ID);
    }

    private NotificationManager getManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

}
