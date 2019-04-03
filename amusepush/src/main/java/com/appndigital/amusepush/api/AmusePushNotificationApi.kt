package com.appndigital.amusepush.api

import com.appndigital.amusepush.api.model.ResponseSendTokenDto
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AmusePushNotificationApi {

    @GET("mobile/get_token_from_app?")
    fun getUserTokenApp(@Query("app_tag") appTag: String): Single<Response<String>>


    @POST("api/terminaux/new_item?")
    @Multipart
    fun registerUserWithToken(@Query("user_token") userToken: String,
                              @Part("id_tel") idTel: RequestBody,
                              @Part("app") app: RequestBody,
                              @Part("os") os: RequestBody,
                              @Part("token") userTokenNotification: RequestBody,
                              @Part("app_vers") app_vers: RequestBody,
                              @Part("app_install") app_install: RequestBody,
                              @Part("app_last_opening") app_last_opening: RequestBody,
                              @Part("id_user_client") idUser: RequestBody): Single<Response<ResponseSendTokenDto>>


    @POST("api/abonnements/new_item?")
    @Multipart
    fun subscribeTag(@Query("user_token") userToken: String,
                     @Part("terminal") terminal: RequestBody,
                     @Part("tag") tag: RequestBody,
                     @Part("app") app: RequestBody,
                     @Part("tag_name") tagName: RequestBody): Single<Response<Any>>

    @POST("api/abonnements/del_item?")
    @Multipart
    fun unsubscribeTag(@Query("user_token") userToken: String,
                       @Part("terminal") terminal: RequestBody,
                       @Part("tag") tag: RequestBody,
                       @Part("app") app: RequestBody): Single<Response<Any>>

}