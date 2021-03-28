package com.example.jankenteamb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jankenteamb.R
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestoreAdapter
import kotlinx.android.synthetic.main.item_add_frame.view.*

class AddFrameAdapter : RecyclerView.Adapter<AddFrameAdapter.ViewHolder>() {
    private val listShop = mutableListOf<ShopListDataFirestoreAdapter>()
    private lateinit var listener: OnClickListenerCallback

    fun setOnClickListener(listenerCallback: OnClickListenerCallback) {
        this.listener = listenerCallback
    }

    fun addData(listShop: List<ShopListDataFirestoreAdapter>) {
        this.listShop.clear()
        this.listShop.addAll(listShop)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(shopListFirestore: ShopListDataFirestoreAdapter) {
            view.tv_title_frame.text = shopListFirestore.title
            Glide.with(view)
                .load(shopListFirestore.frameUri)
                .into(view.iv_item_frame)

            if (shopListFirestore.used) {
                view.btn_gunakan_frame.visibility = View.GONE
                view.btn_gunakan_frame.isClickable = false
                view.btn_digunakan_frame.visibility = View.VISIBLE

            } else {
                view.btn_gunakan_frame.visibility = View.VISIBLE
                view.btn_gunakan_frame.isClickable = true
                view.btn_digunakan_frame.visibility = View.GONE
                listener.let {
                    view.btn_gunakan_frame.setOnClickListener {
                        listener.onClickListenerCallback(shopListFirestore)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_frame, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listShop.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listShop[position])
    }

    interface OnClickListenerCallback {
        fun onClickListenerCallback(shopListFirestore: ShopListDataFirestoreAdapter)
    }

}