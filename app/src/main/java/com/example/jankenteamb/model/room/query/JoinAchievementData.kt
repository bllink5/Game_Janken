package com.example.jankenteamb.model.room.query

data class JoinAchievementData (
    val achievement_title: String,
    val achievement_max: Int,
    val achievement_point: Int,
    val uid: String,
    val achievement_url: String,
    val achievement_progress: Int,
    val achievement_claim: String,
    val achievement_id: Int
)
