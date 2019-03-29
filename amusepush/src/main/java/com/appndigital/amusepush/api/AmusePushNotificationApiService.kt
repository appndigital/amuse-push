package com.appndigital.amusepush.api

import io.reactivex.Completable

interface AmusePushNotificationApiService {

    fun registerUserWithToken(userTokenNotification: String, idUser: String): Completable
    fun subscribeUserTag(idTag: Int, tagName: String): Completable
    fun unsubscribeUserTag(idTag: Int): Completable
}