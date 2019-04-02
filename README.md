
# AmusePush

AmusePush is a funny wordplay in French to say "Amuse bouche"

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
First step you need to follow 

* [Firebase](https://firebase.google.com/docs/android/setup/) - Cloud Messaging

AmusePush requires this dependencies in gradle :

```
implementation 'com.google.firebase:firebase-messaging:17.3.4'
implementation 'com.google.firebase:firebase-core:16.0.7'
implementation 'com.squareup.retrofit2:retrofit:2.4.0'
implementation 'com.squareup.retrofit2:adapter-rxjava2:2.4.0'
implementation 'com.squareup.retrofit2:converter-moshi:2.4.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
implementation 'io.reactivex.rxjava2:rxkotlin:2.3.0'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
```

### Installing

Now you can write this lines

```
implementation 'com.github.appndigital:amuse-push:1.0.2'
```
first you to need to extend your Application class to AmusePushApp class (OPTIONAL)

```
class MyApp : AmusePusApp() {
...
}
```
add in you manifest 

```
<application
android:name="com.mypackage.MyApp"
...
</application>
```
or if you have not Custom Application class

```
<application
android:name="com.appndigital.amusepush.AmusePushApp"
...
</application>
```
you need to create a Service extend AmusePushMessagingService like this

```
class NotificationMessageService : AmusePushMessagingService() {

    override val activityTolaunch: Class<*> = HomeActivity::class.java
}
```

don't forget add this in your manifest :
```
...
 <service android:name=".vigisnap.NotificationMessageService">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

in your App or Activity init Firebase :
```
FirebaseApp.initializeApp(this)
```


## Exemple of use with RX in an Activity

```
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

```

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Jitpack](https://jitpack.io/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Appndigital** - *Initial work* - [AppNdigital](https://www.appndigital.com/)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details



