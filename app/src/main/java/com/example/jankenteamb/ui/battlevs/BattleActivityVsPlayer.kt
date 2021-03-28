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
import kotlinx.android.synthetic.main.activity_battle_vs_player.*
import kotlinx.android.synthetic.main.activity_battle_vs_player.btn_back
import kotlinx.android.synthetic.main.activity_battle_vs_player.tv_draw_multi
import org.koin.android.ext.android.inject

class BattleActivityVsPlayer : AppCompatActivity() {
    private var pickPlayerOne = ""
    private var pickPlayerTwo = ""
    private var view1: View? = null
    private var view2: View? = null
    private var state1 = true
    private var state2 = true
    private var tag = ""
    private val pickList = mutableListOf<View>()
    private lateinit var auth : FirebaseAuth

    //menggantikan presenter
    private val factory by inject<BattleViewModel.Factory>()
    private lateinit var battleVsPlayerViewModel: BattleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle_vs_player)
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
        auth = FirebaseAuth.getInstance()

        //Inisialisasi viewModel
        battleVsPlayerViewModel = ViewModelProvider(
            this,
            factory
        ).get(BattleViewModel::class.java)

        //INISIALISASI SEMUA OBSERVER
        //untuk meng-observe userLiveData jika ada perubahan data
        battleVsPlayerViewModel.userLiveData.observe(this, userLiveData)

        //mengobserve historyResult dan memasukkan data history dan mengupdate data user
        // ke firebase jika ada perubahan, fungsi ini sebelumnya ada di dalam updateUserData
        // di BattlePresenter
        battleVsPlayerViewModel.historyResult.observe(this, Observer {
            battleVsPlayerViewModel.addHistoryToFirebase("Multi Player", it)
        })

        //mengobserve battleResult jika ada perubahan, maka tv_result akan diubah
        //sesuai dengan hasil battleResult
        battleVsPlayerViewModel.battleResult.observe(this, showResult)

        battleVsPlayerViewModel.errorLiveData.observe(this, onError)


        //mengantikan profilePresenter.getUserDataFromRoom("battle") di atas
        battleVsPlayerViewModel.getUserDataFromRoom()

        val player1List =
            mutableListOf<ImageView>(iv_rock_one_multi, iv_paper_one_multi, iv_scissor_one_multi)
        val player2List =
            mutableListOf<ImageView>(iv_rock_two_multi, iv_paper_two_multi, iv_scissor_two_multi)


        player1List.forEach { pick ->
            pick.setOnClickListener {
                if (state1) {
                    pickList.add(it)
                    pickPlayerOne = pick.contentDescription.toString()
                    view1 = it
                    state1 = false
                    Log.d(tag, "you choose $pickPlayerOne")
                    if (pickPlayerTwo != "") {
                        pickList.forEach {
                            it.background = getDrawable(R.drawable.bg_battle_pick)
                        }

                        battleVsPlayerViewModel.getResult(
                            tv_username_one_multi.text.toString(),
                            tv_username_two_multi.text.toString(),
                            pickPlayerOne,
                            pickPlayerTwo
                        )

                    } else {
                        Toast.makeText(this, "Player 2 choose", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Refresh dulu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        player2List.forEach { pick ->
            pick.setOnClickListener {
                if (state2) {
                    pickList.add(it)
                    pickPlayerTwo = pick.contentDescription.toString()
                    view2 = it
                    state2 = false
                    Log.d(tag, "lawan pilih $pickPlayerTwo")
                    if (pickPlayerOne != "") {
                        pickList.forEach { view ->
                            view.background = getDrawable(R.drawable.bg_battle_pick)
                        }

                        battleVsPlayerViewModel.getResult(
                            tv_username_one_multi.text.toString(),
                            tv_username_two_multi.text.toString(),
                            pickPlayerOne,
                            pickPlayerTwo
                        )
                    } else {
                        Toast.makeText(this, "Player 1 silahkan memilih", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Refresh dulu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }


        btn_reset_multi.setOnClickListener {
            pickPlayerOne = ""
            pickPlayerTwo = ""
            resetBackground(view1, view2)
            pickList.clear()
            state1 = true
            state2 = true
            tv_result_multi.text = "VS"
            Log.d(tag, "RESET")
        }
    }

    private fun resetBackground(view1: View?, view2: View?) {
        pickList.forEach {
            it.background = getDrawable(android.R.color.transparent)
        }
        pickList.clear()
    }

    //untuk merubah tv_result menggantikan fun showResult di atas
    private val showResult = Observer<String> { result ->
        runOnUiThread {
            tv_result_multi.text = result
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
            tv_username_one_multi.text = auth.currentUser?.displayName!!
            tv_draw_multi.text = userData.draw.toString()
            tv_win_multi.text = userData.win.toString()
            tv_lose_multi.text = userData.lose.toString()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
    }
}