
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
implementation 'com.github.appndigital:amuse-push:1.0.1'
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
 private fun notifyMe() {
        val amusePushNotificationApiService: AmusePushNotificationApiService = AmusePushNotificationApiServiceImpl(this)
        val amusePushNotificationService: AmusePushNotificationService = AmusePushNotificationServiceImpl()
        var disposable  = GooglePlayHelper.verifyGooglePlayService(this)
            .andThen(amusePushNotificationService.getToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable { token ->
                amusePushNotificationApiService.registerUserWithToken(token, "217")
            }
            .subscribeBy(
                onComplete = {
                    Log.e("Activity", "Connecté au push notif")
                },
                onError = { exception ->

                    Log.e("Activity", "Error on token = ${exception.localizedMessage}")

                    when (exception) {
                        DeviceUnsupportedException() -> {
                            Toast.makeText(
                                this,
                                "votre portable ne supporte pas Google service",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        GooglePlayServicesOutDatedException(), GooglePlayServicesNotInstalledException() -> {
                            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                        }
                        else -> Toast.makeText(this, "error inconnue", Toast.LENGTH_SHORT).show()
                    }
                }
            )
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



