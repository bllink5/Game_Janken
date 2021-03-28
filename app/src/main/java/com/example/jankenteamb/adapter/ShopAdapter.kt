package com.example.jankenteamb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jankenteamb.R
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestoreAdapter
import kotlinx.android.synthetic.main.item_shop_list.view.*

class ShopAdapter : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    private val listShop = mutableListOf<ShopListDataFirestoreAdapter>()
    private lateinit var listener : OnClickListenerCallback

    fun setOnClickListener (listenerCallback : OnClickListenerCallback){
        this.listener = listenerCallback
    }

    fun addData (listShop : List<ShopListDataFirestoreAdapter>){
        this.listShop.clear()
        this.listShop.addAll(listShop)
        notifyDataSetChanged()
    }

    inner class ViewHolder (val view : View) : RecyclerView.ViewHolder(view){
        fun bind (shopListFirestore: ShopListDataFirestoreAdapter){
            view.tv_title_frame.text = shopListFirestore.title
            
            Glide.with(view).load(shopListFirestore.frameUri).into(view.iv_item_frame)
            view.tv_price_frame.text = shopListFirestore.price.toString()
            listener.let {
                view.btn_beli_frame.setOnClickListener {
                    listener.onClickListenerCallback(shopListFirestore)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listShop.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listShop[position])
    }

    interface OnClickListenerCallback{
        fun onClickListenerCallback(shopListFirestore: ShopListDataFirestoreAdapter)
    }

}