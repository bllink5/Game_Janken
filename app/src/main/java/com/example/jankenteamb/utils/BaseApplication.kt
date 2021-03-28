package com.example.jankenteamb.utils

import android.app.Application
import androidx.room.Room
import com.example.jankenteamb.db.AppDatabase
import com.example.jankenteamb.di.firebaseRepositoryModule
import com.example.jankenteamb.di.koinModule
import com.example.jankenteamb.di.roomRepositoryModule
import com.example.jankenteamb.di.viewModelModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        startKoin {
            androidContext(this@BaseApplication)
            modules(module {
                single {
                    Room.databaseBuilder(applicationContext, AppDatabase::class.java, "Janken 3.1")
                        .build()
                }
                single { FirebaseAuth.getInstance() }
                single { Firebase.storage.reference }
                single { DefaultDispatcherProvider() as DispatcherProvider}
            })
            modules(koinModule)
            modules(firebaseRepositoryModule)
            modules(roomRepositoryModule)
            modules(viewModelModule)
        }
    }
}