package com.example.jankenteamb.ui.battlevs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.R
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.viewmodel.BattleViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_battle_vs_com.*
import kotlinx.android.synthetic.main.activity_battle_vs_com.btn_back
import kotlinx.android.synthetic.main.activity_battle_vs_com.tv_draw_multi
import org.koin.android.ext.android.inject

//view dihilangkan
class BattleActivityVsCom : AppCompatActivity() {
    private var pickPlayer = ""
    private var pickComputer = ""
    private var view1: View? = null
    private var view2: View? = null
    private var state1 = true
    private var computer = mutableListOf("rock", "paper", "scissor")
    private var tag = ""
    private lateinit var listPlayer1: MutableList<ImageView>
    private lateinit var auth: FirebaseAuth

    //menggantikan presenter
    private val factory by inject<BattleViewModel.Factory>()
    private lateinit var battleViewModel: BattleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle_vs_com)
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
        auth = FirebaseAuth.getInstance()
        //Inisialisasi viewModel
        battleViewModel = ViewModelProvider(
            this,
            factory
        ).get(BattleViewModel::class.java)

        //INISIALISASI SEMUA OBSERVER
        //untuk meng-observe userLiveData jika ada perubahan data
        battleViewModel.userLiveData.observe(this, userLiveData)

        //mengobserve historyResult dan memasukkan data history dan mengupdate data user
        // ke firebase jika ada perubahan, fungsi ini sebelumnya ada di dalam updateUserData
        // di BattlePresenter
        battleViewModel.historyResult.observe(this, Observer {
            battleViewModel.addHistoryToFirebase("Single Player", it)
        })

        //mengobserve battleResult jika ada perubahan, maka tv_result akan diubah
        //sesuai dengan hasil battleResult
        battleViewModel.battleResult.observe(this, showResult)

        battleViewModel.errorLiveData.observe(this, onError)

        //        profilePresenter.setView(this)
//                profilePresenter.getUserDataFromRoom("battle")

        //mengantikan profilePresenter.getUserDataFromRoom("battle") di atas
        battleViewModel.getUserDataFromRoom()

        listPlayer1 = mutableListOf(iv_rock_one, iv_paper_one, iv_scissor_one)

        btn_back.setOnClickListener {
            onBackPressed()
        }

        listPlayer1.forEach {
            it.setOnClickListener { view ->
                if (state1) {
                    view.background = getDrawable(R.drawable.bg_battle_pick)
                    pickPlayer = view.contentDescription.toString()
                    pickComputer = computer.random()
                    when (pickComputer) {
                        "paper" -> {
                            iv_paper_two.background = getDrawable(R.drawable.bg_battle_pick)
                            view2 = iv_paper_two
                        }
                        "rock" -> {
                            iv_rock_two.background = getDrawable(R.drawable.bg_battle_pick)
                            view2 = iv_rock_two
                        }
                        else -> {
                            iv_scissor_two.background = getDrawable(R.drawable.bg_battle_pick)
                            view2 = iv_scissor_two
                        }
                    }

                    //mengantikan result.theResult di atas
                    battleViewModel.getResult(
                        tv_username_one.text.toString(),
                        tv_username_two.text.toString(),
                        pickPlayer,
                        pickComputer
                    )

                    view1 = view
                    state1 = false
                    Log.d(tag, "Pemain choose $pickPlayer")
                    Log.d(tag, "Computer choose $pickComputer")
                } else {
                    Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
                }
            }
        }


        btn_reset.setOnClickListener {
            pickPlayer = ""
            pickComputer = ""
            resetBackground(view1, view2)
            state1 = true
            tv_result.text = "VS"
            Log.d(tag, "RESET")
        }
    }

    private fun resetBackground(view1: View?, view2: View?) {
        view1?.background = getDrawable(android.R.color.transparent)
        view2?.background = getDrawable(android.R.color.transparent)
    }

    //untuk merubah tv_result menggantikan fun showResult di atas
    private val showResult = Observer<String> { result ->
        runOnUiThread {
            tv_result.text = result
        }
    }

    //menggantikan fun onError
    val onError = Observer<String> {
        runOnUiThread {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    //mengantikan fungsi onSuccessOnBattle di atas
    val userLiveData = Observer<UserData> { userData ->
        runOnUiThread {
            tv_username_one.text = auth.currentUser?.displayName!!
            tv_draw_multi.text = userData.draw.toString()
            tv_win.text = userData.win.toString()
            tv_lose.text = userData.lose.toString()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
    }


}
