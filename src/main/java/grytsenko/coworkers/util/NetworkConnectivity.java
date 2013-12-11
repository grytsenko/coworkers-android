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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helps to check that network connectivity is suitable.
 */
public class NetworkConnectivity {

    private ConnectivityManager manager;

    /**
     * Creates helper.
     * 
     * @param context
     *            the context to use.
     */
    public NetworkConnectivity(Context context) {
        manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Checks that we have suitable connection to network.
     * 
     * @param wifiOnly
     *            if only connection to Wi-Fi is suitable.
     * 
     * @return <code>true</code> if connection is suitable and
     *         <code>false</code> otherwise.
     */
    public boolean isSuitable(boolean wifiOnly) {
        NetworkInfo connectivity = manager.getActiveNetworkInfo();

        if (connectivity == null || !connectivity.isConnected()) {
            return false;
        }

        if (wifiOnly && connectivity.getType() != ConnectivityManager.TYPE_WIFI) {
            return false;
        }

        return true;
    }

}
