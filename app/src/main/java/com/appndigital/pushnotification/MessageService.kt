package com.appndigital.pushnotification

import com.appndigital.amusepush.AmusePushMessagingService

class MessageService : AmusePushMessagingService() {

    override val activityTolaunch: Class<*> = MainActivity::class.java
}