package com.example.jankenteamb.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.jankenteamb.ui.landingPage.LandingPage1Fragment
import com.example.jankenteamb.ui.landingPage.LandingPage2Fragment
import com.example.jankenteamb.ui.landingPage.LandingPage3Fragment

class ViewPagerAdapter(fa:FragmentActivity) : FragmentStateAdapter(fa){

    private val fragment = listOf(
        LandingPage1Fragment(),
        LandingPage2Fragment(),
        LandingPage3Fragment()
    )

    override fun getItemCount(): Int {
        return fragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragment[position]
    }
}