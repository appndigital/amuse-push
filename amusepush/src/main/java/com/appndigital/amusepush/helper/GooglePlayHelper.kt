package com.appndigital.amusepush.helper

import android.content.Context
import com.appndigital.amusepush.exceptions.DeviceUnsupportedException
import com.appndigital.amusepush.exceptions.GooglePlayServicesNotInstalledException
import com.appndigital.amusepush.exceptions.GooglePlayServicesOutDatedException
import com.appndigital.amusepush.exceptions.UnknownErrorException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.reactivex.Completable


class GooglePlayHelper {

    companion object {

        /* exection possible
              DeviceUnsupportedException::class,
              GooglePlayServicesOutDatedException::class,
              GooglePlayServicesNotInstalledException::class,
              UnknownErrorException::class
              */

        fun verifyGooglePlayService(context: Context): Completable = Completable.create { emitter ->

            val googleAPI = GoogleApiAvailability.getInstance()
            val status = googleAPI.isGooglePlayServicesAvailable(context)


            when (status) {
                ConnectionResult.SUCCESS -> {
                    emitter.onComplete()
                }
                ConnectionResult.SERVICE_MISSING -> {
                    throw GooglePlayServicesNotInstalledException()
                }
                ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                    throw GooglePlayServicesOutDatedException()
                }
                else -> {
                    if (googleAPI.isUserResolvableError(status)) {
                        emitter.onError(DeviceUnsupportedException())
                    }
                    emitter.onError(UnknownErrorException("Unknown error status = ${googleAPI.getErrorString(status)}"))
                }
            }

        }

    }
}