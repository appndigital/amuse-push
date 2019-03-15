package com.appndigital.pushnotification.registernotification

import io.reactivex.Completable
import io.reactivex.Single

interface AmusePushNotificationService {

    fun getToken(): Single<String>
    fun removeToken(): Completable
}