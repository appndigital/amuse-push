package com.appndigital.pushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.appndigital.pushnotification.api.AmusePushNotificationApiService
import com.appndigital.pushnotification.api.AmusePushNotificationApiServiceImpl
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

class AmusePushMessagingService : FirebaseMessagingService() {

    private val amusePushNotificationApiService: AmusePushNotificationApiService = AmusePushNotificationApiServiceImpl()
    var disposable: Disposable? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("AmusePushMessaging", "onMessageReceived From ${remoteMessage?.from}")

        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d("AmusePushMessaging", "onMessageReceived data ${remoteMessage.data}")
            sendNotification("TODO  from DATA")
        }

        remoteMessage?.notification?.let {
            Log.d("AmusePushMessaging", "onMessageReceived notification ${it.body}")
            sendNotification(it.body!!)
        }
    }

    override fun onNewToken(token: String?) {
        //stocker l'id de l'utilisateur dans les shared et le mettre ici avec un flatmap
        token?.let {
            disposable = amusePushNotificationApiService.sendTokenNotificationForUser(token, "217")
                .subscribeBy(
                    onComplete = {
                        Log.d("AmusePushMessaging", "on New token success save on server")
                    },
                    onError = {
                        Log.e("AmusePushMessaging", "Error on token = ${it.localizedMessage}")
                    }
                )
        }

    }


    private fun sendNotification(messageBody: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channelId = getString(R.string.default_notification_channel_id)
            // Create channel to show notifications.
            try {
                notificationManager.getNotificationChannel(channelId)
            } catch (e: Exception) {
                val channelName = getString(R.string.default_notification_channel_name)

                notificationManager?.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }


        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.pick_up)
            .setContentTitle(getString(R.string.title))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }

}