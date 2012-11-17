Android Drupal SDK
================================

Android Drupal SDK is an easy to use Android client for REST servers based on Drupal and Services 3.

It is built at [Demotix.com](http://www.demotix.com/ "Demotix.com") by Alexandru Badiu and used in the [Demotix Android app](https://play.google.com/store/apps/details?id=com.demotix).

While being used in an application it is work in progress and implements currently only a small number of the standard functions offered by the Services module.

It uses [Android Asynchronous Http Client](http://loopj.com/android-async-http/).

General implementation
======================

There is a base class, ServicesClient, which takes care of storing the session information and make the POST, GET and DELETE calls.

Each service group (System services, User services) is implemented in a separate class which uses the base class to make the relevant calls.

Usage
=====

    ServicesClient client;

    // Create a REST client that connects to example.com/api/mobile
    client = new ServicesClient("http://www.example.com", "api/mobile");

    // Optionally you can add a persistent cookie store to save the session and reuse it between application runs
    cookieStore = new PersistentCookieStore(this);
    client.setCookieStore(cookieStore);

Implementing new services
=========================

Uploading large files
=====================