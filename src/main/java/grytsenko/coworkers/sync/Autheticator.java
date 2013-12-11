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

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import grytsenko.coworkers.R;
import grytsenko.coworkers.ui.SignInActivity;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Allows to create the single account using {@link SignInActivity}.
 */
public class Autheticator extends AbstractAccountAuthenticator {

    private static final String TAG = Autheticator.class.getName();

    private Context context;

    public Autheticator(Context context) {
        super(context);

        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
            String accountType, String authTokenType,
            String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType(context
                .getString(R.string.account_type));

        if (accounts.length > 0) {
            Log.d(TAG, "User account already exists.");
            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE,
                    context.getString(R.string.account_exists));
            return bundle;
        }

        Intent intent = new Intent(context, SignInActivity.class);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
            Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
            String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
            Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
            Account account, String[] features) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
            Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        return null;
    }

}
