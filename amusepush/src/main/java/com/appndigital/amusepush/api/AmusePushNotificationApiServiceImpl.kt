package com.appndigital.amusepush.api

import android.content.Context
import android.util.Log
import com.appndigital.amusepush.Constants
import com.appndigital.amusepush.R
import com.appndigital.amusepush.exceptions.GetTagFromApiException
import com.appndigital.amusepush.exceptions.SendTokenApiException
import com.appndigital.amusepush.helper.VariantHelper
import com.appndigital.amusepush.helper.toRequestBody
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class AmusePushNotificationApiServiceImpl(private val context: Context) : AmusePushNotificationApiService {

    private val retrofitAmusePushNotificationApi: AmusePushNotificationApi = buildRetrofitAmusePushApi()
    val ID_TEL = context.getString(R.string.id_tel)
    val APP_TAG = context.getString(R.string.app_tag)
    val NUM_APP = context.resources.getInteger(R.integer.num_app)
    val OS = context.resources.getInteger(R.integer.os)

    override fun registerUserWithToken(userTokenNotification: String, idUser: String): Completable =
        Completable.create { emitter ->
            retrofitAmusePushNotificationApi.getUserTokenApp(APP_TAG)
                .flatMap { response ->
                    Log.d("AmusePushNotification", "onSuccess = $response")
                    if (response.isSuccessful) {
                        saveTokenWebbApp(response.body()!!)
                        retrofitAmusePushNotificationApi.registerUserWithToken(
                            response.body()!!,
                            ID_TEL.toRequestBody(),
                            NUM_APP.toRequestBody(),
                            OS.toRequestBody(),
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
                            saveIdTerminal(response.body()!!.id)
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

    override fun subscribeUserTag(idTag: Int, tagName: String): Completable = Completable.create { emitter ->
        retrofitAmusePushNotificationApi.subscribeTag(
            getTokenWebApp(),
            getIdTerminal().toRequestBody(),
            idTag.toRequestBody(),
            NUM_APP.toRequestBody(),
            tagName.toRequestBody()
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { response ->
                    Log.e("AmusePushSubscribeTag", "onSuccess = $response")
                    if (response.isSuccessful) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(SendTokenApiException(response.message()))
                    }
                },
                onError = {
                    Log.e("AmusePushApi", "error subscribeUserTag = ${it.localizedMessage}")
                    emitter.onError(it)
                }
            )
    }

    override fun unsubscribeUserTag(idTag: Int): Completable = Completable.create { emitter ->
        retrofitAmusePushNotificationApi.unsubscribeTag(
            getTokenWebApp(),
            getIdTerminal().toRequestBody(),
            idTag.toRequestBody(),
            NUM_APP.toRequestBody()
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { response ->
                    Log.e("AmusePushUnSubscribeTag", "onSuccess = $response")
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
        val baseUrl = VariantHelper.getBackendEndPoint(context)
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

    private fun saveTokenWebbApp(tokenWebbApp: String) {
        val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.TOKEN_WEB_APP_PREF_KEY, tokenWebbApp).apply()

    }

    private fun saveIdTerminal(id: Int) {
        val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit().putInt(Constants.TERMINAL_ID_PREF_KEY, id).apply()

    }

    private fun getIdTerminal(): Int {
        val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return prefs.getInt(Constants.TERMINAL_ID_PREF_KEY, 0)
    }

    private fun getTokenWebApp(): String {
        val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return prefs.getString(Constants.TOKEN_WEB_APP_PREF_KEY, "")
    }

}