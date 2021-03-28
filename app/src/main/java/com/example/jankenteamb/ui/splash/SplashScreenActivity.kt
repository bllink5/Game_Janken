package com.example.jankenteamb.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jankenteamb.R
import com.example.jankenteamb.async.FirebaseAsync
import com.example.jankenteamb.async.FirebaseAsyncListener
import com.example.jankenteamb.ui.landingPage.LandingPageActivity
import com.example.jankenteamb.ui.menu.MenuActivity
import com.example.jankenteamb.utils.ACHIEVEMENT_DATA_DOCUMENT
import com.example.jankenteamb.utils.FirebaseHelper
import com.example.jankenteamb.utils.GAME_DATA_COLLECTION
import com.example.jankenteamb.utils.PreferenceHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.koin.android.ext.android.inject

class SplashScreenActivity : AppCompatActivity(), FirebaseAsyncListener {
    private val async by inject<FirebaseAsync>()
    private val preferenceHelper by inject<PreferenceHelper>()
    private val firebaseHelper by inject<FirebaseHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val documentRef = Firebase.firestore.collection(GAME_DATA_COLLECTION).document(
            ACHIEVEMENT_DATA_DOCUMENT
        )

        if (preferenceHelper.isFirstLoad) {
            async.setListener(this)
            async.execute(documentRef)
            preferenceHelper.isFirstLoad = false
        }else{
            if (firebaseHelper.isFirebaseLogin()) {
                pb_splash.max = 100
                pb_splash.progress = 100
                Handler().postDelayed({
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                }, 3500)
            } else {
                pb_splash.max = 100
                pb_splash.progress = 100
                Handler().postDelayed({
                    startActivity(Intent(this, LandingPageActivity::class.java))
                    finish()
                }, 3500)
            }
        }

    }

    override fun onStart(msg: String) {
        pb_splash.progress = 10
        tv_progress.text = msg
    }

    override fun onProgress(progress: Int) {
        pb_splash.visibility = View.VISIBLE
        pb_splash.max = 100
        pb_splash.progress = progress
    }

    override fun onComplete(msg: String) {
        tv_progress.text = msg
        Handler().postDelayed({
            startActivity(Intent(this, LandingPageActivity::class.java))
            finish()
        }, 3000)
    }
}