package com.voidberg.drupaldroid;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.*;

public class ServicesClient {
    private String url;
    public static AsyncHttpClient client = new AsyncHttpClient();

    public ServicesClient(String server, String base) {
        this.url = server + '/' + base + '/';
        client.setTimeout(60000);
    }

    public void setCookieStore(PersistentCookieStore cookieStore) {
        client.setCookieStore(cookieStore);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return this.url + relativeUrl;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.post(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.put(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }
}
