package com.appndigital.pushnotification.api

import io.reactivex.Completable

interface AmusePushNotificationApiService {

    fun sendTokenNotificationForUser(userTokenNotification: String,
                                     idUser: String): Completable
}