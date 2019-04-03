package com.appndigital.amusepush

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.appndigital.amusepush.api.AmusePushNotificationApiService
import com.appndigital.amusepush.api.AmusePushNotificationApiServiceImpl
import com.appndigital.amusepush.exceptions.DeviceUnsupportedException
import com.appndigital.amusepush.exceptions.GooglePlayServicesNotInstalledException
import com.appndigital.amusepush.exceptions.GooglePlayServicesOutDatedException
import com.appndigital.amusepush.helper.GooglePlayHelper
import com.appndigital.amusepush.helper.AmusePushUtils
import com.appndigital.pushnotification.registernotification.AmusePushNotificationService
import com.appndigital.pushnotification.registernotification.AmusePushNotificationServiceImpl
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.FirebaseApp
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

open class AmusePushApp : Application() {

    val TAG = "AmusePushApp"

    val compositeDisposable = CompositeDisposable()

    private val amusePushNotificationService: AmusePushNotificationService = AmusePushNotificationServiceImpl()
    private lateinit var amusePushNotificationApiService: AmusePushNotificationApiService
    lateinit var activityTolaunchForNotification: Class<*>

    companion object {
        var version = "1.0"

        @JvmStatic
        fun defineVersion(context: Context) {
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                version = pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        AmusePushUtils.saveDateLastOpening(this)
        FirebaseApp.initializeApp(this)
        amusePushNotificationApiService = AmusePushNotificationApiServiceImpl(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }


    fun initAmusePush(): Completable = Completable.create { emitter ->
        val prefs = getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val fcmToken = prefs.getString(Constants.FCM_TOKEN_PREFERENCES_KEY, "")
        val advertisingIdClient = prefs.getString(Constants.ADVERTISING_ID_CLIENT_PREFERENCES_KEY, "")

        Observable.just(advertisingIdClient.isNotEmpty())
            .subscribeOn(Schedulers.io())
            .flatMapSingle { hasAdvertisingIdClient ->
                if (!hasAdvertisingIdClient) {
                    getAdvertisingIdClient()
                } else {
                    Single.just("")
                }
            }.flatMapSingle { advertisingId ->
                if (advertisingId.isNotEmpty()) {
                    Log.d(TAG, "Success $advertisingId")
                    prefs.edit()
                        .putString(Constants.ADVERTISING_ID_CLIENT_PREFERENCES_KEY, advertisingId)
                        .apply()
                }
                Single.just(fcmToken.isNotEmpty())
            }
            .flatMapSingle { hasFcmToken ->
                if (!hasFcmToken) {
                    GooglePlayHelper.verifyGooglePlayService(this)
                        .andThen(amusePushNotificationService.getToken())
                } else {
                    Single.just("")
                }
            }.subscribeBy(
                onNext = { fcmToken ->
                    if (fcmToken.isNotEmpty()) {
                        Log.d(TAG, "Success $fcmToken")
                        prefs.edit()
                            .putString(Constants.FCM_TOKEN_PREFERENCES_KEY, fcmToken)
                            .apply()
                    }
                },
                onComplete = {
                    emitter.onComplete()

                },
                onError = { exception ->
                    when (exception) {
                        DeviceUnsupportedException() -> {
                            Log.e(TAG, "votre portable ne supporte pas Google service")
                        }

                        GooglePlayServicesOutDatedException(), GooglePlayServicesNotInstalledException() -> {
                            Log.e(TAG, "Google play il faut télécharger la nouvelle version ")
                            //todo créer une variable static needGooglePlay et créer une abstract actiivty qui au onCreate va checker la variable
                            //todo et donc proposer de ddl google plays service si besoin
                            //GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                        }
                        else -> {
                            Log.e(TAG, "Error inconnue ${exception.localizedMessage} ")
                        }
                    }
                    emitter.onError(exception)
                }
            )
    }


    fun saveUserId(idUser: String) {
        val prefs = getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(Constants.USER_ID_PREFERENCES_KEY, idUser)
            .apply()
    }


    fun sendFCMTokenToServer(): Completable {
        return amusePushNotificationApiService
            .sendFcmTokenToServer()
    }


    fun subscribeTag(idTag: Int, tagName: String): Completable {
        return amusePushNotificationApiService
            .subscribeUserTag(idTag, tagName)
    }


    fun unsubscribeTag(idTag: Int): Completable {
        return amusePushNotificationApiService
            .unsubscribeUserTag(idTag)
    }

    private fun getAdvertisingIdClient(): Single<String> = Single.create { emitter ->
        try {
            val id = AdvertisingIdClient.getAdvertisingIdInfo(this).id
            emitter.onSuccess(id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed getAdvertisingIdClient: ${e.localizedMessage}")
        }

    }

}