package com.example.jankenteamb.ui.menu.mainMenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.bumptech.glide.Glide
import com.example.jankenteamb.R
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.ui.achievement.AchievementActivity
import com.example.jankenteamb.ui.battlevs.BattleActivityVsCom
import com.example.jankenteamb.ui.battlevs.BattleActivityVsPlayer
import com.example.jankenteamb.ui.profile.ProfileActivity
import com.example.jankenteamb.utils.FirebaseHelper
import com.example.jankenteamb.viewmodel.MainMenuViewModel
import kotlinx.android.synthetic.main.fragment_main_menu.*
import org.koin.android.ext.android.inject

class MainMenuFragment : Fragment() {
    //    private val logoutPresenter by inject<LogoutPresenter>()
    private val firebaseHelper by inject<FirebaseHelper>()
//    private val profilePresenter by inject<ProfilePresenter>()

    private val factory by inject<MainMenuViewModel.Factory>()
    private lateinit var viewModel: MainMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = Slide(Gravity.END).apply { duration = 200 }

        viewModel = ViewModelProvider(
            this,
            factory
        ).get(MainMenuViewModel::class.java)
        viewModel.errorLiveData.observe(viewLifecycleOwner, onError)
        viewModel.userLiveData.observe(viewLifecycleOwner, onSuccessMenu)
        viewModel.uriLiveData.observe(viewLifecycleOwner, onSuccessDownloadPhotoProfile)

        viewModel.getUserDataFromRoom()
        viewModel.downloadPhotoProfile()

        iv_singleplayer_main.setOnClickListener {
            iv_singleplayer_main.playAnimation()
            startActivity(Intent(activity, BattleActivityVsCom::class.java))
        }

        iv_multiplayer_main.setOnClickListener {
            iv_multiplayer_main.playAnimation()
            startActivity(Intent(activity, BattleActivityVsPlayer::class.java))
        }

        tv_achievment_main.setOnClickListener {
            startActivity(Intent(activity, AchievementActivity::class.java))
        }
        constraintLayout.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.downloadPhotoProfile()
        tv_username_main.text = firebaseHelper.getUsername()
//        profilePresenter.downloadPhotoProfile()
    }

    val onError = Observer<String> { msg ->
        activity?.runOnUiThread {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    private val onSuccessDownloadPhotoProfile = Observer<Uri> { uri ->
        activity?.runOnUiThread {
            Glide.with(activity?.baseContext!!)
                .load(uri)
                .fitCenter()
                .circleCrop()
                .into(iv_photo_main)
        }
    }

    private val onSuccessMenu = Observer<UserData> { userData ->
        activity?.runOnUiThread {

            tv_username_main.text = firebaseHelper.getUsername()
            tv_level_main.text = userData.level.toString()
            pb_level_main.progress = userData.exp
            pb_level_main.max = when (userData.level) {
                1 -> 5
                2 -> 10
                3 -> 15
                4 -> 25
                5 -> 40
                else -> if (userData.exp > 100) 100 else userData.exp
            }
        }
    }

}