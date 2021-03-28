package com.example.jankenteamb.ui.menu.friend.friendlist

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
import com.example.jankenteamb.adapter.FriendListAdapter
import com.example.jankenteamb.adapter.FriendListAdapter.OnClickListenerCallback
import com.example.jankenteamb.model.firestore.friendlist.FriendListDataWithUri
import com.example.jankenteamb.viewmodel.FriendListViewModel
import kotlinx.android.synthetic.main.fragment_friend_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class FriendListFragment : Fragment() {
    private lateinit var friendListAdapter: FriendListAdapter

    private val factory by inject<FriendListViewModel.Factory>()
    private lateinit var friendListViewModel: FriendListViewModel

    private val list: MutableList<FriendListDataWithUri> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendListViewModel = ViewModelProvider(
            this,
            factory
        ).get(FriendListViewModel::class.java)

        friendListViewModel.errorLiveData.observe(viewLifecycleOwner, onError)
        friendListViewModel.successDeleteFriendLiveData.observe(viewLifecycleOwner, onSuccessDeleteFriend)
        friendListViewModel.friendListLiveData.observe(viewLifecycleOwner, onGetFriendList)

        friendListAdapter = FriendListAdapter(list)
        CoroutineScope(Dispatchers.Main).launch {
            friendListViewModel.getFriendList()
        }

        rv_friend_list.layoutManager = LinearLayoutManager(requireContext())
        rv_friend_list.adapter = friendListAdapter

        showLoading(true)
        showEmpty(true)

        friendListAdapter.setOnClickListener(object : OnClickListenerCallback<FriendListDataWithUri> {
            override fun onDeleteClick(data: FriendListDataWithUri, position: Int) {
                deleteFriend(data.uid)
            }
        })
    }

    override fun onResume() {
        friendListViewModel.getFriendList()
        super.onResume()
    }

    fun deleteFriend(friendUid: String) {
        friendListViewModel.deleteFriend(friendUid)
    }

    val onError = Observer<String>{
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private val onSuccessDeleteFriend = Observer<String>{
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            friendListViewModel.getFriendList()
        }
    }

    private val onGetFriendList = Observer<List<FriendListDataWithUri>>{ friendList ->
        this.requireActivity().runOnUiThread {
            if (friendList.isNotEmpty()){
                friendListAdapter.updateListData(friendList)
                showLoading(false)
                showEmpty(false)
            }else{
                showLoading(false)
                showEmpty(true)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            pb_friend_list.visibility = View.VISIBLE
        } else {
            pb_friend_list.visibility = View.GONE
        }
    }

    private fun showEmpty(state: Boolean) {
        if (state) {
            tv_empty.visibility = View.VISIBLE
            imageView.visibility = View.VISIBLE
            tv_description.visibility = View.VISIBLE
            rv_friend_list.visibility = View.GONE
        } else {
            imageView.visibility = View.GONE
            tv_description.visibility = View.GONE
            tv_empty.visibility = View.GONE
            rv_friend_list.visibility = View.VISIBLE
        }
    }
}