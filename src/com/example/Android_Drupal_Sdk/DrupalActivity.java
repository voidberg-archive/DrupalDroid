package com.example.drupaldroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.voidberg.drupaldroid.ServicesClient;
import com.voidberg.drupaldroid.UserServices;

public class DrupalActivity extends Activity {
    Activity activity;
    ProgressDialog progressDialog;

    String url = "http://example.com";
    String apiEndpoint = "api/mobile";
    String drupalUser = "user";
    String drupalPass = "pass";

    String TAG = "drupal-sdk-example";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        ServicesClient client;
        client = new ServicesClient(url, apiEndpoint);

        UserServices us;

        us = new UserServices(client);

        progressDialog = ProgressDialog.show(activity, "", "Logging you in", true, false);

        us.Login(drupalUser, drupalPass, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Response is a JSON string containing sessid, session_name and a user object
                Log.v(TAG, response);

                new AlertDialog.Builder(activity).setMessage("Login was successful.").setPositiveButton("OK", null).setCancelable(true).create().show();
            }

            @Override
            public void onFailure(Throwable e, String response) {
                Log.v(TAG, e.getMessage());
                Log.v(TAG, response);

                new AlertDialog.Builder(activity).setMessage("Login failed.").setPositiveButton("OK", null).setCancelable(true).create().show();
            }

            @Override
            public void onFinish() {
                progressDialog.hide();
                progressDialog.dismiss();
            }
        });
    }
}
