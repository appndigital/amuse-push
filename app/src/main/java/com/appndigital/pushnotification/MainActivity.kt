package com.appndigital.pushnotification

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.appndigital.amusepush.AmusePushApp
import com.appndigital.amusepush.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    lateinit var amusePushApp: AmusePushApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amusePushApp = application as AmusePushApp
        val prefs = getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)

        //init amusePush at the begining better in splashscreen
        progressBar.visibility = View.VISIBLE
        amusePushApp.initAmusePush()
            .subscribeBy(
                onComplete = {
                    //display if token and advertising is save
                    val fcmToken = prefs.getString(Constants.FCM_TOKEN_PREFERENCES_KEY, "")
                    val advertisingIdClient = prefs.getString(Constants.ADVERTISING_ID_CLIENT_PREFERENCES_KEY, "")
                    textView4.text = "$advertisingIdClient\n $fcmToken"
                    progressBar.visibility = View.INVISIBLE
                }
            ).addTo(compositeDisposable)

        sendFcmToServer.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            //important save an id user before save token to server
            amusePushApp.saveUserId("12354")
            amusePushApp.sendFCMTokenToServer()
                .subscribeBy(
                    onComplete = {
                        progressBar.visibility = View.INVISIBLE
                        textView.text = "Fcm envoyé au serveur"
                    }
                ).addTo(compositeDisposable)
        }


        subscribeTag.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            //for subscribe
            amusePushApp
                .subscribeTag(5, "tagname")
                .subscribeBy(
                    onComplete = {
                        progressBar.visibility = View.INVISIBLE
                        textView2.text = "tag souscris"
                    }
                ).addTo(compositeDisposable)
        }
        unsubscribeTag.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            //for unsubscribe by tag
            amusePushApp.unsubscribeTag(5)
                .subscribeBy(
                    onComplete = {
                        progressBar.visibility = View.INVISIBLE
                        textView3.text = "tag non souscris"
                    }
                ).addTo(compositeDisposable)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
