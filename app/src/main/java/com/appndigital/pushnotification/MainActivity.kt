package com.appndigital.pushnotification

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.appndigital.pushnotification.api.AmusePushNotificationApiService
import com.appndigital.pushnotification.api.AmusePushNotificationApiServiceImpl
import com.appndigital.pushnotification.exceptions.DeviceUnsupportedException
import com.appndigital.pushnotification.exceptions.GooglePlayServicesNotInstalledException
import com.appndigital.pushnotification.exceptions.GooglePlayServicesOutDatedException
import com.appndigital.pushnotification.helper.GooglePlayHelper
import com.appndigital.pushnotification.registernotification.AmusePushNotificationService
import com.appndigital.pushnotification.registernotification.AmusePushNotificationServiceImpl
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val amusePushNotificationApiService: AmusePushNotificationApiService = AmusePushNotificationApiServiceImpl()
    private val amusePushNotificationService: AmusePushNotificationService = AmusePushNotificationServiceImpl()
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            disposable = GooglePlayHelper.verifyGooglePlayService(this)
                .andThen(amusePushNotificationService.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable { token ->
                    amusePushNotificationApiService.sendTokenNotificationForUser(token, "217")
                }
                .subscribeBy(
                    onComplete = {
                        progressBar.visibility = View.GONE
                        textView.text = "Connecté au push notif"
                    },
                    onError = { exception ->

                        progressBar.visibility = View.GONE
                        Log.e("MainActivity", "Error on token = ${exception.localizedMessage}")

                        when (exception) {
                            DeviceUnsupportedException() -> {
                                Toast.makeText(this, "votre portable ne supporte pas Google service", Toast.LENGTH_SHORT).show()
                            }

                            GooglePlayServicesOutDatedException(), GooglePlayServicesNotInstalledException() -> {
                                GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                            }
                            else -> Toast.makeText(this, "error inconnue", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
        }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
        disposable = null
    }
}
