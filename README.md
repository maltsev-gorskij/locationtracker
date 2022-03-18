## Application
Create application which:
- Track location and sends them to cloud database.
- Show all those locations on the map.

## Required functionality
- Implement users registration/login via email and password.
- If app can't send location to server cache it locally and send later.
- Tracking period should be each 5 second.
- Tracking should be restored after reboot and update.
- When app is on foreground, listen for changes in cloud database, download new locations and show them on map.

## Technology stack
- Language - Kotlin
- Architecture - Single activity
- Architecture pattern - MVVM
- UI pattern - Single-Activity
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
    - [View Binding](https://developer.android.com/topic/libraries/view-binding)
    - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
    - [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
    - [Room](https://developer.android.com/training/data-storage/room)
- [Firebase Auth](https://firebase.google.com/products/auth)
- [Firebase RealtimeDatabase](https://firebase.google.com/products/realtime-database)
- [RxJava2](https://github.com/ReactiveX/RxJava)
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)
- [RxKotlin](https://github.com/ReactiveX/RxKotlin)
- [Dagger2](https://github.com/google/dagger)

## Project assembly notes
- In project root folder create apikeys.properties file. Populate file with api keys: 
    - **yandexMapApiKey** for Yandex MapKit
    - **firebaseRealtimeDatabaseUrl** for Firebase Authentication and Firebase Realtime Database

- In app module root place google services configuration json 
    - google-services.json

- In project root folder place firebase project name configuration file
    - .firebaserc