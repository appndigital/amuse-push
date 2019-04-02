package com.appndigital.pushnotification

import com.appndigital.amusepush.AmusePushApp

class MyApp : AmusePushApp() {

    override fun onCreate() {
        super.onCreate()
        activityTolaunchForNotification = MainActivity::class.java
    }

}