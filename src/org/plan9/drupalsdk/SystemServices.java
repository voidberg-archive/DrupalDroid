package org.plan9.drupalsdk;

import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONObject;

public class SystemServices {
    private ServicesClient client;

    public SystemServices(ServicesClient c) {
        client = c;
    }

    public void Connect(AsyncHttpResponseHandler responseHandler) {
        client.post("system/connect", new JSONObject(), responseHandler);
    }
}