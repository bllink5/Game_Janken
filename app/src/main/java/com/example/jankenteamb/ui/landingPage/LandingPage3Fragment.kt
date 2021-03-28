package com.example.jankenteamb.ui.landingPage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jankenteamb.R
import com.example.jankenteamb.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_landing_page3.*

class LandingPage3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing_page3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_nextLand.setOnClickListener {
            startActivity(Intent(context,LoginActivity::class.java))
            activity?.finish()
        }
    }
}