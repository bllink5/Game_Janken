package com.example.jankenteamb.ui.landingPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_landing_page.*

//private const val NUM_PAGES = 3
class LandingPageActivity : AppCompatActivity() {
    //private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        //viewPager = findViewById(R.id.viewPager)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        worm_dots.setViewPager2(viewPager)

    }
}