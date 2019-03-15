package com.appndigital.pushnotification.api

import android.util.Log
import com.appndigital.pushnotification.Constants
import com.appndigital.pushnotification.exceptions.GetTagFromApiException
import com.appndigital.pushnotification.exceptions.SendTokenApiException
import com.appndigital.pushnotification.helper.VariantHelper
import com.appndigital.pushnotification.helper.toRequestBody
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class AmusePushNotificationApiServiceImpl : AmusePushNotificationApiService {

    private val retrofitAmusePushNotificationApi: AmusePushNotificationApi = buildRetrofitAmusePushApi()

    override fun sendTokenNotificationForUser(userTokenNotification: String, idUser: String): Completable =
        Completable.create { emitter ->
            retrofitAmusePushNotificationApi.getUserTokenApp(Constants.APP_TAG).flatMap { response ->
                Log.d("AmusePushNotification", "onSuccess = $response")
                if (response.isSuccessful) {
                    retrofitAmusePushNotificationApi.sendTokenNotificationForUser(
                        response.body()!!,
                        Constants.ID_TEL.toRequestBody(),
                        Constants.NUM_APP.toRequestBody(),
                        Constants.OS.toRequestBody(),
                        userTokenNotification.toRequestBody(),
                        idUser.toRequestBody()
                    )
                } else {
                    throw GetTagFromApiException()
                }
            }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onSuccess = { response ->
                        Log.e("AmusePushNotification", "onSuccess = $response")
                        if (response.isSuccessful) {
                            emitter.onComplete()
                        } else {
                            emitter.onError(SendTokenApiException(response.message()))
                        }

                    },
                    onError = {
                        Log.e("AmusePushApi", "error send token = ${it.localizedMessage}")
                        emitter.onError(it)
                    }
                )
        }


    private fun buildRetrofitAmusePushApi(): AmusePushNotificationApi {
        val baseUrl = VariantHelper.getBackendEndPoint()
        val moshi = Moshi.Builder()
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
           // .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(AmusePushNotificationApi::class.java)
    }

}