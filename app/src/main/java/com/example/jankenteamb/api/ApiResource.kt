package com.example.jankenteamb.api

import com.example.jankenteamb.BuildConfig
import com.example.jankenteamb.model.api.PayloadNotif
import com.example.jankenteamb.utils.BASE_URL_NOTIF
import com.example.jankenteamb.utils.SERVER_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiResource {
    private fun servicesNotif(): ApiServices {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_NOTIF)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiServices::class.java)
    }

    fun pushNotif(payloadNotif: PayloadNotif) = servicesNotif()
        .pushNotif(SERVER_KEY, payloadNotif)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}