package com.example.jankenteamb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jankenteamb.R
import com.example.jankenteamb.model.room.user.UserData
import kotlinx.android.synthetic.main.item_top_score.view.*

class TopScoreAdapter : RecyclerView.Adapter<TopScoreAdapter.ViewHolder>() {

    private var listTopScore = mutableListOf<UserData>()

    fun addData(listTopScore: List<UserData>){
        this.listTopScore.clear()
        this.listTopScore.addAll(listTopScore)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(topScore : UserData, position: Int){
            view.tv_result.text = "${position.plus(1)}. ${topScore.username}"
            view.tv_xp.text = topScore.exp.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopScoreAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_score, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listTopScore.size

    override fun onBindViewHolder(holder: TopScoreAdapter.ViewHolder, position: Int) {
        holder.bind(listTopScore[position], position)
    }

}