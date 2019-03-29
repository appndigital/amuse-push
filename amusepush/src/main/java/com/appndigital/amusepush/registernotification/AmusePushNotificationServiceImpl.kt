package com.appndigital.pushnotification.registernotification

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Single


class AmusePushNotificationServiceImpl : AmusePushNotificationService {

    private val TAG: String = "AmusePushNotification"

    //On génère un token
    override fun getToken(): Single<String> = Single.create { emitter ->
        FirebaseInstanceId.getInstance()
            .instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    emitter.onError(task.exception!!)
                } else {
                    // Get new Instance ID token
                    val token = task.result!!.token
                    Log.d(TAG,"token from firebase instance id =  $token")
                    emitter.onSuccess(token)
                }
            }
    }

    //on supprime tout les token
    override fun removeToken(): Completable = Completable.create { emitter ->
        try {
            FirebaseInstanceId.getInstance()
                .deleteInstanceId()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}