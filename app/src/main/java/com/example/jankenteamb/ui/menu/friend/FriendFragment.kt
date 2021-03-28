package com.example.jankenteamb.ui.menu.friend

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.FriendViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_friend.*

class FriendFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterTransition = Slide(Gravity.END).apply { duration = 200 }

//        enterTransition = TransitionInflater.from(activity).inflateTransition(R.transition.slide_right)

        view_pager_friend.adapter = FriendViewPagerAdapter(requireContext(), childFragmentManager)
        tab_layout.setupWithViewPager(view_pager_friend)
    }
}