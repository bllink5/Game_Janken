package com.example.jankenteamb.adapter

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.jankenteamb.R
import com.example.jankenteamb.ui.menu.friend.friendlist.FriendListFragment
import com.example.jankenteamb.ui.menu.friend.friendsuggestion.FriendSuggestionFragment

class FriendViewPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val TAB_TITLES = intArrayOf(
        R.string.tab_1_label,
        R.string.tab_2_label)

//    @DrawableRes
//    private val TAB_ICON = intArrayOf(
//        R.drawable.bot_nav_friend,
//        R.drawable.bot_nav_friend
//    )

    private val fragment = listOf(
        FriendListFragment(),
        FriendSuggestionFragment()
    )

    override fun getItem(position: Int): Fragment {
        return fragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return fragment.size
    }
}