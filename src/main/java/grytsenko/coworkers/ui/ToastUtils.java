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

import android.content.Context;
import android.widget.Toast;

/**
 * Utilities for toasts.
 */
public final class ToastUtils {

    /**
     * Shows toast.
     * 
     * @param context
     *            the context to use.
     * @param messageId
     *            the identifier of message.
     */
    public static void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    private ToastUtils() {
    }

}
