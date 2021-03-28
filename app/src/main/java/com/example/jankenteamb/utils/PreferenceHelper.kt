package com.example.jankenteamb.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper (context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(APPS_NAME, Context.MODE_PRIVATE)

    var isDataChanged : Boolean
        get(){
            return sharedPreferences.getBoolean(IS_DATA_CHANGED,false)
        }

        set(value) {
            sharedPreferences.edit().apply{
                putBoolean(IS_DATA_CHANGED, value)
                apply()
            }
        }

    var isFirstLoad : Boolean
        get() {
            return sharedPreferences.getBoolean(IS_FIRST_LOAD, true)
        }
        set(value) {
            sharedPreferences.edit().apply{
                putBoolean(IS_FIRST_LOAD, value)
                apply()
            }
        }
//
//    var win : Int
//        get(){
//            return sharedPreferences.getInt(USER_TOKEN_KEY, 0)
//        }
//
//        set(value){
//            sharedPreferences.edit().apply {
//                putString(USER_TOKEN_KEY, value)
//                apply()
//            }
//        }
}