package com.example.jankenteamb.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.jankenteamb.R
import io.karn.notify.Notify
import java.lang.Exception

class NotifyWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        return try {
            Notify
                .with(applicationContext)
                .content {
                    title = "Game Notification"
                    text = "Take Some Rest, You Have Been Play For Too Long"
                }
                .header {
                    icon = R.drawable.ic_android_black
                    headerText = "Alert"
                }
                .alerting("") {
                    channelImportance = Notify.IMPORTANCE_HIGH
                }
                .show()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}