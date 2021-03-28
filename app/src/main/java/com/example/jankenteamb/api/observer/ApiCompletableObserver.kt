package com.example.jankenteamb.api.observer

import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class ApiCompletableObserver constructor(private val compositeDisposable: CompositeDisposable):
    CompletableObserver {

    override fun onComplete() {
        onApiSuccess()
    }

    override fun onSubscribe(d: Disposable) {
        compositeDisposable.add(d)
    }

    override fun onError(e: Throwable) {
        onApiError(e)
    }

    abstract fun onApiSuccess()
    abstract fun onApiError(error: Throwable)


}