package com.example.jankenteamb.ui.menu.friend.friendsuggestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.FriendSuggestAdapter
import com.example.jankenteamb.model.firestore.friendlist.FriendListDataWithUri
import com.example.jankenteamb.viewmodel.FriendSuggestViewModel
import kotlinx.android.synthetic.main.fragment_friend_suggestion.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FriendSuggestionFragment : Fragment() {
    private lateinit var friendSuggestAdapter: FriendSuggestAdapter

    private val factory by inject<FriendSuggestViewModel.Factory>()
    private lateinit var friendSuggestViewModel: FriendSuggestViewModel
    private val list: MutableList<FriendListDataWithUri> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_suggestion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendSuggestViewModel =
            ViewModelProvider(this, factory).get(FriendSuggestViewModel::class.java)
        friendSuggestViewModel.onErrorLiveData.observe(viewLifecycleOwner, onError)
        friendSuggestViewModel.onSuccessLiveData.observe(viewLifecycleOwner, onSuccessAddFriend)
        friendSuggestViewModel.friendSuggestLiveData.observe(
            viewLifecycleOwner,
            onSuccessGetFriendSuggestion
        )
        friendSuggestAdapter = FriendSuggestAdapter(list)

        rv_friend_suggest.layoutManager = LinearLayoutManager(requireContext())
        rv_friend_suggest.adapter = friendSuggestAdapter
        CoroutineScope(Dispatchers.Main).launch {
            friendSuggestViewModel.getFriendSuggest()
        }

        friendSuggestAdapter.setOnClickListener(object :
            FriendSuggestAdapter.OnClickListenerCallback<FriendListDataWithUri> {
            override fun onAddClick(data: FriendListDataWithUri, position: Int) {
                addFriend(data)
            }
        })
    }

    override fun onResume() {
        friendSuggestViewModel.getFriendSuggest()
        super.onResume()
    }

    private fun addFriend(friendListData: FriendListDataWithUri) {
        val photoUrl = friendListData.photoUrl
        val username = friendListData.username
        val uid = friendListData.uid
        friendSuggestViewModel.addFriend(photoUrl, username, uid)
    }

    private val onSuccessGetFriendSuggestion =
        Observer<List<FriendListDataWithUri>> { listFriendSuggest ->
            this.requireActivity().runOnUiThread {
                if (listFriendSuggest.isEmpty()) {
                    tv_empty2.visibility = View.VISIBLE
                    rv_friend_suggest.visibility = View.GONE
                } else {
                    tv_empty2.visibility = View.GONE
                    rv_friend_suggest.visibility = View.VISIBLE
                    friendSuggestAdapter.updateListData(listFriendSuggest)
                }
            }
        }

    private val onSuccessAddFriend = Observer<String> { msg ->
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            friendSuggestViewModel.getFriendSuggest()
        }
    }

    private val onError = Observer<String> { msg ->
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

    }


}