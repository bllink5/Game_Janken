package com.example.jankenteamb.model.battlevs

//interface untuk menggantikan class BattleResult karena
// ViewModel harus extend dari ViewModel() dan di kotlin tidak bisa extend dari 2 class
// maka BattleResult dibuat sebagai interface
interface Battle {

    fun theResult(
        playerOne: String,
        playerTwo: String, pone: String, ptwo: String
    ): Map<String,String> {
        val janken = listOf("rock", "paper", "scissor")
        if (pone == janken[0] && ptwo == janken[2] ||
            pone == janken[1] && ptwo == janken[0] ||
            pone == janken[2] && ptwo == janken[1]
        ) {
            return mapOf("showResult" to "$playerOne\nWin","historyResult" to "Player Win")
        } else if (
            pone == janken[0] && ptwo == janken[1] ||
            pone == janken[1] && ptwo == janken[2] ||
            pone == janken[2] && ptwo == janken[0]
        ) {
            return mapOf("showResult" to "$playerTwo\nWin","historyResult" to "Player Lose")
        } else {
            return mapOf("showResult" to "Draw","historyResult" to "Player Draw")
        }
    }

    fun userLevel(exp: Int): Int {
        return when (exp) {
            in 0..5 -> 1
            in 6..10 -> 2
            in 11..15 -> 3
            in 16..25 -> 4
            in 26..40 -> 5
            else -> 6
        }
    }
}