package com.example.jankenteamb.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jankenteamb.R
import com.example.jankenteamb.model.firestore.GameHistoryFirestoreData
import kotlinx.android.synthetic.main.item_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var listHistory = mutableListOf<GameHistoryFirestoreData>()

    fun addData(listHistory: List<GameHistoryFirestoreData>){
        this.listHistory.clear()
        this.listHistory.addAll(listHistory)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view:View) : RecyclerView.ViewHolder(view){
        fun bind(history : GameHistoryFirestoreData){
            Log.d("historyBind", history.toString())
            val date = history.gameDate?.toDate()
            date?.let{
                view.tv_tanggal.text = SimpleDateFormat("yyyy/MM/dd", Locale("ID")).format(it)
            }
            view.tv_hasil.text = history.gameResult
            view.tv_mode.text = history.gameMode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listHistory.size

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

}