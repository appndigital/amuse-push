package com.appndigital.pushnotification.api

import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AmusePushNotificationApi {

    @GET("mobile/get_token_from_app?")
    fun getUserTokenApp(@Query("app_tag") appTag: String): Single<Response<String>>

    @POST("api/terminaux/new_item?")
    @Multipart
    fun sendTokenNotificationForUser(@Query("user_token") userToken: String,
                                     @Part("id_tel") idTel: RequestBody,
                                     @Part("app") app: RequestBody,
                                     @Part("os") os: RequestBody,
                                     @Part("token") userTokenNotification: RequestBody,
                                     @Part("id_user_client") idUser: RequestBody): Single<Response<Any>>

}