package com.example.jankenteamb.ui.achievement

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.AchievementAdapter
import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.viewmodel.AchievementViewModel
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_achivement.*
import org.koin.android.ext.android.inject

class AchievementActivity : AppCompatActivity() {
    private lateinit var achievementAdapter: AchievementAdapter

    private val factory by inject<AchievementViewModel.Factory>()
    private lateinit var achievementViewModel: AchievementViewModel
//    private val achievementPresenter by inject<AchievementPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achivement)
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
        //inisialisasi viewmodel
        achievementViewModel =
            ViewModelProvider(this, factory)
                .get(AchievementViewModel::class.java)

        //observer
        achievementViewModel.errorLiveData.observe(this, onError)
        achievementViewModel.successLiveData.observe(this, onSuccess)
        achievementViewModel.showPointLiveData.observe(this, showPoint)
        achievementViewModel.getAchievementLiveData.observe(this, onSuccessGetAchievementProgress)

        achievementViewModel.getAchievementProgress()
        achievementViewModel.getPointData()


//        achievementPresenter.setView(this)
//        achievementPresenter.getPointData()
//        achievementPresenter.getAchievementProgress()
        achievementAdapter = AchievementAdapter()
        rv_achievement_list.layoutManager = LinearLayoutManager(this)
        rv_achievement_list.adapter = achievementAdapter
        achievementAdapter.setOnClickListener(object : AchievementAdapter.OnClickListenerCallback {
            override fun onClickListenerCallback(achievement: JoinAchievementData) {
                showDialog(achievement)
            }
        })
        btn_back.setOnClickListener{
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
    }

    fun showDialog(achievement: JoinAchievementData) {
        val alertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Claim Achievement")
                setMessage("Anda ingin claim achievement ini?")
                setPositiveButton("Ya") { _, _ ->
                    achievementViewModel.claimAchievement(achievement)
                    achievementViewModel.updatePoint(achievementViewModel.showPointLiveData.value!! + achievement.achievement_point)
                    achievementViewModel.getAchievementProgress()
                }
                setNegativeButton("Tidak") { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private val onSuccess = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }

    val onError = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }

    private val showPoint = Observer<Int> {
        runOnUiThread {
            tv_achievement_coin.text = it.toString()
        }
    }

    private val onSuccessGetAchievementProgress = Observer<MutableList<JoinAchievementData>> {
        this.runOnUiThread {
            if (it.size == 0) {
                tv_achievement_empty.isVisible = true
            } else {
                achievementAdapter.addData(it)
            }
        }
    }
}