package com.example.jankenteamb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jankenteamb.R
import com.example.jankenteamb.model.firestore.friendlist.FriendListDataWithUri
import kotlinx.android.synthetic.main.item_friend_suggest.view.*

class FriendSuggestAdapter(private val listFriendSuggest: MutableList<FriendListDataWithUri>) :
    RecyclerView.Adapter<FriendSuggestAdapter.ViewHolder>() {
    private lateinit var listener : OnClickListenerCallback<FriendListDataWithUri>

    fun updateListData(data: List<FriendListDataWithUri>){
        this.listFriendSuggest.clear()
        this.listFriendSuggest.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listenerCallback : OnClickListenerCallback<FriendListDataWithUri>) {
        this.listener = listenerCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_suggest, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listFriendSuggest.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFriendSuggest[position], position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: FriendListDataWithUri, position: Int) {
            Glide.with(itemView).load(item.photoUri).into(itemView.iv_user)
            itemView.tv_user.text = item.username
            itemView.iv_add_friend.setOnClickListener {
                listener.onAddClick(item,position)
            }
        }
    }

    interface OnClickListenerCallback<T> {
        fun onAddClick(data: T, position: Int)
    }

}