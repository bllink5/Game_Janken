package com.example.jankenteamb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jankenteamb.R
import com.example.jankenteamb.model.room.query.JoinAchievementData
import kotlinx.android.synthetic.main.item_achievment_list.view.*

class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.ViewHolder>(){
    private var listAchievement = mutableListOf<JoinAchievementData>()
    private lateinit var listener : OnClickListenerCallback

    fun setOnClickListener (listenerCallback : OnClickListenerCallback){
        this.listener = listenerCallback
    }

    fun addData(listAchievement: List<JoinAchievementData>){
        this.listAchievement.clear()
        this.listAchievement.addAll(listAchievement)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(achievement: JoinAchievementData){
            Glide.with(view)
                .load(achievement.achievement_url)
                .into(view.iv_title_achivement)
            view.tv_title_achievment.text = achievement.achievement_title
            view.pb_achievment.progress = achievement.achievement_progress
            view.pb_achievment.max = achievement.achievement_max
            view.tv_pb_achievment.text = achievement.achievement_title
            view.tv_coin_achievment.text = achievement.achievement_point.toString()
            if(achievement.achievement_progress == achievement.achievement_max && achievement.achievement_claim != "claimed"){
                listener.let {
                    view.btn_claim.setOnClickListener {
                        listener.onClickListenerCallback(achievement)
                    }
                }
            }else{
                view.btn_claim.visibility = View.GONE
            }


        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AchievementAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievment_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listAchievement.size

    override fun onBindViewHolder(holder: AchievementAdapter.ViewHolder, position: Int) {
        holder.bind(listAchievement[position])
    }

    interface OnClickListenerCallback{
        fun onClickListenerCallback(achievement: JoinAchievementData)
    }
}