DrupalDroid
================================

DrupalDroid is an easy to use Android client for REST servers based on Drupal and Services 3.

It is built at [Demotix.com](http://www.demotix.com/ "Demotix.com") by [Alexandru Badiu](http://ctrlz.ro) and used in the [Demotix Android app](https://play.google.com/store/apps/details?id=com.demotix).

While being used in an application it is work in progress and implements currently only a small number of the standard functions offered by the Services module. Feel free to fork the project, add more resources and make a pull request.

It uses [Android Asynchronous Http Client](http://loopj.com/android-async-http/).

Latest version is [1.2](https://github.com/voidberg/DrupalDroid/raw/master/releases/drupaldroid-1.2.jar).

Features
========

* Easy to use.
* Supports basic authentication.
* Persistent cookies.
* Tiny overhead.
* HTTP requests happen outside the UI thread.
* Smart retries, gzip, threadpool.

Implemented resources
======================

* User - login, logout
* System - connect

Example
=======

The example supplied shows how to perform a login. It was built with IntelliJ Idea 12.

Authors
=======

* [Alexandru Badiu](http://ctrlz.ro)
* [Luciano Vitti](https://github.com/xAnubiSx)

General implementation
======================

The ServicesClient base class takes care of storing the session information and making the POST, GET and DELETE calls.

Each service group (System services, User services) is implemented in a separate class which uses the base class to make the relevant calls.

Usage
=====

Create a REST client that connects to example.com/api/mobile:

    ServicesClient client;
    client = new ServicesClient("http://www.example.com", "api/mobile");

Set basic auth credentials:
    
    client.setBasicAuth("username","password/token");

Add a persistent cookie store to save the session and reuse it between application runs:
    
    cookieStore = new PersistentCookieStore(this);
    client.setCookieStore(cookieStore);

Get the CSRF token if needed:
	
	client.getToken(new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(String response) {
        String token = response;
        client.setToken(token);
      }

      @Override
      public void onFailure(Throwable e, String response) {
        Util.Log("token onFailure");
      }
    });


Create system and user services:

    UserServices us;
    SystemServices ss;

    us = new UserServices(client);
    ss = new SystemServices(client);

Call system.connect and check if we are logged in:
    
    JsonHttpResponseHandler connectHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(JSONObject response) {
            boolean loggedin = false;
            try {
                JSONObject user = response.getJSONObject("user");
                int uid = user.getInt("uid");
                if (uid > 0) {
                    loggedin = true;
                }
                else {
                    loggedin = false;
                }
            } catch (JSONException e) {
                loggedin = false;
            }

            if (loggedin) {
                // User is already logged in, do something 
            }
            else {
                // User is not logged in, display login activity or automatically login
            }
        }

        @Override
        public void onFailure(Throwable e, JSONObject response) {
            // System.Connect call failed
        }

        @Override
        public void onFinish() {
        }
    };

    ss.Connect(connectHandler);

Call user.login:

    JsonHttpResponseHandler loginHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(JSONObject response) {
            boolean error = false;
            try {
                JSONObject user = response.getJSONObject("user");
          
            } catch (JSONException e) {
                error = true;
            }

            if (error) {
                // A JSON error occured
            }
            else {
                // User has logged in
            }
        }

        @Override
        public void onFailure(Throwable e, JSONObject response) {
            // Username or password were incorrect
        }

        @Override
        public void onFinish() {
            activity.hideProgressDialog();
        }
    };

    activity.showProgressDialog("Logging you in");
    us.Login("username", "password");

Implementing new services
=========================

New services should live in a class named after their group (System, Node, User) and Services (e.g. NodeServices). 

It should have a ServicesClient property and it's constructor should set that property. 

Parameters should be packaged in a JSONObject.

Calls to services should be made using the provided methods in ServicesClient:

* get(String method, JSONObject params, AsyncHttpResponseHandler responseHandler)
* post(String method, JSONObject params, AsyncHttpResponseHandler responseHandler)
* delete(String method, JSONObject params, AsyncHttpResponseHandler responseHandler)


Uploading large files
=====================

Android Asynchronous Http Client supports file uploading but reads the whole contents of the files in memory 
which will not work for large files, causing your application to crash with an out of memory exception.

The solution is to go a bit lower level and use the underlying HttpClient to do the upload attaching the files as 
InputStreams which will perform a streaming upload.

    HttpParams httpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParams, 900 * 1000);
    HttpConnectionParams.setSoTimeout(httpParams, 900 * 1000);

    HttpClient httpclient = ServicesClient.client.getHttpClient();
    HttpContext httpContext = ServicesClient.client.getHttpContext();

    HttpPost httppost = new HttpPost("http://www.example.com/api/mobile/service/upload");
    httppost.setParams(httpParams);

    MultipartEntity entity = new MultipartEntity();
    // Add parameters to the post body
    entity.addPart("param1", new StringBody(param1.toString(),"application/json", Charset.forName("UTF-8")));
    entity.addPart("param2", new StringBody(param2.toString(),"application/json", Charset.forName("UTF-8")));

    InputStream istream1 = new FileInputStream("file1.jpg");
    entity.addPart("file1", new InputStreamBody(istream1, "file1"));
    InputStream istream2 = new FileInputStream("file2.jpg");
    entity.addPart("file2", new InputStreamBody(istream2, "file2"));

    httppost.setEntity(entity);
    HttpResponse response = httpclient.execute(httppost, httpContext);


Progress callback for upload
============================

HttpClient does not have built-in support for performing an upload with a progress callback and implementing one 
isn't trivial.

For file uploading you can implement a custom multi part entity that hooks into the file stream read operation and 
counts how much data has been read.

    int totalUploadSize = 0;

    CustomMultiPartEntity postEntity = new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {
        @Override
        public void transferred(long num) {
            currentUploadSize = num;
            updateUploadProgress((int) ((num / (float) totalUploadSize) * 100));
            updateNotification((int) ((num / (float) totalUploadSize) * 100));
        }
    });

    try {
        postEntity.addPart("param1", new StringBody(param1.toString(),"application/json", Charset.forName("UTF-8")));
        postEntity.addPart("param2", new StringBody(param2.toString(),"application/json", Charset.forName("UTF-8")));

        totalUploadSize = postEntity.getContentLength();
        for (Map.Entry<String, String> entry : files.entrySet()) {
            try {
                InputStream istream = new FileInputStream(entry.getValue());
                postEntity.addPart(entry.getKey(), new InputStreamBody(istream, entry.getKey()));
                totalUploadSize += new File(entry.getValue()).length();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        httppost.setEntity(postEntity);
    } 
    catch (UnsupportedEncodingException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    
    HttpResponse response = httpclient.execute(httppost, httpContext);


Applications using this library
===============================

* [Demotix for Android](https://play.google.com/store/apps/details?id=com.demotix)
* Send me a message to have your app added here.
