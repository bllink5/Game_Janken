package com.example.jankenteamb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jankenteamb.R
import com.example.jankenteamb.model.firestore.friendlist.FriendListDataWithUri
import kotlinx.android.synthetic.main.item_friend_list.view.*

class FriendListAdapter(private val listFriend: MutableList<FriendListDataWithUri>) :
    RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {
    private lateinit var listener: OnClickListenerCallback<FriendListDataWithUri>

    fun updateListData(data: List<FriendListDataWithUri>) {
        this.listFriend.clear()
        this.listFriend.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listenerCallback: OnClickListenerCallback<FriendListDataWithUri>) {
        this.listener = listenerCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listFriend.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFriend[position], position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: FriendListDataWithUri, position: Int) {
            Glide.with(itemView).load(item.photoUri).into(itemView.iv_user_profile)
            itemView.tv_username.text = item.username
            itemView.setOnClickListener {
                listener.onDeleteClick(item, position)
            }
        }
    }

    interface OnClickListenerCallback<T> {
        fun onDeleteClick(data: T, position: Int)
    }

}