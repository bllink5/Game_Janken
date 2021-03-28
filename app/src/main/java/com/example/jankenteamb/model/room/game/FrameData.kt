package com.example.jankenteamb.model.room.game

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FrameData(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "frame_id") val frameId: Int,
    @ColumnInfo(name = "frame_title") val frameTitle: String,
    @ColumnInfo(name = "frame_url") val frameUrl: String,
    @ColumnInfo(name = "frame_price") val framePrice: Int
)