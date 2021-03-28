package com.example.jankenteamb.async

import android.os.AsyncTask
import android.util.Log
import com.example.jankenteamb.db.AppDatabase
import com.example.jankenteamb.model.firestore.achievementData.AchievementDataFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseAsync(database: AppDatabase) :
    AsyncTask<DocumentReference, Int, String>() {
    private lateinit var listener: FirebaseAsyncListener
    private val achievementDao = database.achievementDao()
    private val compositeDisposable = CompositeDisposable()

    fun setListener(listener: FirebaseAsyncListener) {
        this.listener = listener
    }

    override fun doInBackground(vararg params: DocumentReference?): String {
        var size = 0
        var total = 0
        var allAchievement: AchievementDataFirestore
        //membuat koneksi ke firebase dan mengambil data dari document achievementData di collection gameData
        params[0]!!.get().addOnCompleteListener {
            it.addOnSuccessListener { documentSnapshot ->
                //mengubah tipe data returnnya dari DocumentSnapshot ke data class AchievementDataFirestore
                allAchievement = documentSnapshot.toObject<AchievementDataFirestore>()!!
                size = allAchievement.achievementList.size - 1

                CoroutineScope(Dispatchers.IO).launch {
                    //memecah allAchievement dan memasukkannya ke room
                    allAchievement.achievementList.forEachIndexed { index, achievement ->
                        //achievement dimasukkan ke room
                        achievementDao.insertAchievement(achievement)
                        total += index
                        publishProgress(((index / size.toFloat()) * 100).toInt())
                    }
                }
            }
        }
        Log.d("Data get", "Data get")

        return "Please wait..."
    }

    //fungsi untuk memasukkan data achievement ke room mengembalikan Disposable yang akan ditampung di compositeDisposable
//    fun insertAchievement(achievementData: AchievementData): Disposable {
//        return achievementDao.insertAchievement(achievementData)
//            .observeOn(Schedulers.io())
//            .subscribeOn(Schedulers.io())
//            .subscribe({
////                view.onSuccess("Data insert done")
//                Log.d("insert to room", "data insert done")
//            }, {
//                Log.d("insert to room", it.message!!)
////                view.onError(it.message!!)
//            })
//
//    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener.onStart("Please wait...")
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        listener.onProgress(values[0]!!)
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        listener.onComplete(result!!)
        compositeDisposable.dispose()
    }

}