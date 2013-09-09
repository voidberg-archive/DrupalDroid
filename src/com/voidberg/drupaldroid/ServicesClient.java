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
    private String rootUrl;
    private String token;

    public static AsyncHttpClient client = new AsyncHttpClient();

    public ServicesClient(String server, String base) {
        this.url = server + '/' + base + '/';
        this.rootUrl = server + '/';
        this.token = "";
        client.setTimeout(60000);
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    private void setHeaders() {
      if (!token.equals("")) {
        client.addHeader("X-CSRF-Token", token);
      }
    }

    public void setCookieStore(PersistentCookieStore cookieStore) {
        client.setCookieStore(cookieStore);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return this.url + relativeUrl;
    }

    private String getAbsoluteRootUrl(String relativeUrl) {
      return this.rootUrl + relativeUrl;
    }

    public void getRoot(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.get(getAbsoluteRootUrl(url), params, responseHandler);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
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
        this.setHeaders();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.put(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }
    
    public void getToken(AsyncHttpResponseHandler responseHandler) {
      this.getRoot("services/session/token", new RequestParams(), responseHandler);
    }
}
