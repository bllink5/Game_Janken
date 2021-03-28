package com.example.jankenteamb.async

interface FirebaseAsyncListener {
    fun onStart(msg: String)
    fun onProgress(progress : Int)
    fun onComplete(msg: String)
}