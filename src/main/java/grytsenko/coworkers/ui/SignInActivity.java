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
import grytsenko.coworkers.web.WebClient;
import grytsenko.coworkers.web.WebException;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Allows user to enter credentials and create new account.
 */
public class SignInActivity extends AccountAuthenticatorActivity {

    private static final String TAG = SignInActivity.class.getName();

    private static final int IS_SYNCABLE = 1;

    private EditText usernameInput;
    private EditText passwordInput;

    private String accountType;

    private String username;
    private String password;

    private SignInTask signInTask;
    private Dialog signInDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_activity);

        accountType = getString(R.string.account_type);

        signInDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.sign_in_wait)
                .setOnCancelListener(new Dialog.OnCancelListener() {

                    public void onCancel(DialogInterface dialog) {
                        signInTask.cancel(true);
                    }

                }).create();

        usernameInput = (EditText) findViewById(R.id.username_input);
        passwordInput = (EditText) findViewById(R.id.password_input);

        final CheckBox showPasswordInput = (CheckBox) findViewById(R.id.show_password_input);
        showPasswordInput.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean checked = showPasswordInput.isChecked();
                passwordInput.setTransformationMethod(checked ? null
                        : new PasswordTransformationMethod());
                passwordInput.setSelection(passwordInput.getText().length());
            }

        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                onSignInStarted();
            }

        });
    }

    /**
     * Performs sign in.
     */
    private void onSignInStarted() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            ToastUtils.showToast(this, R.string.account_exists);
            return;
        }

        username = usernameInput.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.showToast(this, R.string.username_empty);
            return;
        }

        password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showToast(this, R.string.password_empty);
            return;
        }

        signInTask = new SignInTask();
        signInTask.execute();
        signInDialog.show();

        Log.d(TAG, "Sign in started.");
    }

    public void onSignInCompleted(Boolean succeeded) {
        signInDialog.dismiss();

        if (!succeeded) {
            ToastUtils.showToast(this, R.string.sign_in_failed);
            return;
        }

        createAccount();

        username = null;
        password = null;

        Log.d(TAG, "Sign in completed.");
        finish();
    }

    private void createAccount() {
        Log.d(TAG, "Create account.");

        AccountManager manager = AccountManager.get(this);
        Account account = new Account(username, accountType);

        boolean created = manager.addAccountExplicitly(account, password, null);
        if (!created) {
            Log.w(TAG, "Could not create account.");
            ToastUtils.showToast(this, R.string.account_not_created);
            return;
        }

        startSync(account);

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        setAccountAuthenticatorResult(bundle);
    }

    private void startSync(Account account) {
        String authority = ContactsContract.AUTHORITY;
        ContentResolver.setIsSyncable(account, authority, IS_SYNCABLE);
        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.requestSync(account, authority, new Bundle());
    }

    public void onSignInCancelled() {
        Log.d(TAG, "Sign in cancelled.");
    }

    private class SignInTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... args) {
            try {
                WebClient webClient = new WebClient();
                String serviceUrl = getString(R.string.rest_coworkers);
                webClient.getEmployees(serviceUrl, username, password);
                return true;
            } catch (WebException exception) {
                Log.d(TAG, "Sign in failed.", exception);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean authenticated) {
            onSignInCompleted(authenticated);
        }

        @Override
        protected void onCancelled() {
            onSignInCancelled();
        }
    }

}