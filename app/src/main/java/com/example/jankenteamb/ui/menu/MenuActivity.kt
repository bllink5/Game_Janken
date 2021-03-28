package com.example.jankenteamb.ui.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jankenteamb.R
import com.example.jankenteamb.service.NotifyWorker
import kotlinx.android.synthetic.main.activity_menu.*
import java.util.concurrent.TimeUnit

class MenuActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        controller = findNavController(R.id.nav_host_bottom_view)
        bottom_navigation.setupWithNavController(controller)

        val request = PeriodicWorkRequestBuilder<NotifyWorker>(30, TimeUnit.MINUTES)
            .addTag("DONTPLAY")
            .setInitialDelay(120, TimeUnit.MINUTES)
            .build()

        workManager = WorkManager.getInstance(this)
        workManager.enqueue(request)
//        supportFragmentManager.beginTransaction().apply {
//            replace(
//                R.id.layout_container,
//                MainMenuFragment()
//            )
//            commit()
        }

    override fun onDestroy() {
        workManager.cancelAllWorkByTag("DONTPLAY")
        super.onDestroy()
    }
}
