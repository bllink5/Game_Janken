package com.example.jankenteamb.api.observer

import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class ApiSingleObserver<T> constructor(private val compositeDisposable: CompositeDisposable) :
    SingleObserver<T> {

    override fun onSubscribe(d: Disposable) {
        compositeDisposable.add(d)
    }

    override fun onSuccess(t: T) {
        onApiSuccess(t)
    }

    override fun onError(e: Throwable) {
        onApiError(e)
    }

    abstract fun onApiSuccess(data: T)
    abstract fun onApiError(error: Throwable)


}