package com.example.jankenteamb.model.room.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int? = null,
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "uid") var uid: String = "",
    @ColumnInfo(name = "level") var level: Int = 0,
    @ColumnInfo(name = "win") var win: Int = 0,
    @ColumnInfo(name = "draw") var draw: Int = 0,
    @ColumnInfo(name = "lose") var lose: Int = 0,
    @ColumnInfo(name = "point") var point: Int = 0,
    @ColumnInfo(name = "exp") var exp: Int = 0,
    @ColumnInfo(name = "photoUrl") var photoUrl: String = "",
    @ColumnInfo(name = "frameUrl") var frameUrl: String = "",
    @ColumnInfo(name = "tokenNotif") var tokenNotif: String = ""
)