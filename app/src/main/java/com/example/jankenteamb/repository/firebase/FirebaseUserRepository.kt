package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.example.jankenteamb.utils.JANKEN_USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseUserRepository(private val auth: FirebaseAuth) : IFUserRepository {

    override suspend fun addUserData(userData: UserData): RepositoryResult<String> {
        val docRef = Firebase.firestore
            .document("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}")
        return when (val query = docRef.set(userData).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add user data success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateUserData(newUserData: UserData): RepositoryResult<String> {
        val docRef = Firebase.firestore.document("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}")
        return when (val query = docRef.set(newUserData, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update user data success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateUserData(mapUserData: Map<String, String>): RepositoryResult<String> {
        val docRef = Firebase.firestore.document("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}")
        return when (val query = docRef.set(mapUserData, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update user data success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getUserData(): RepositoryResult<UserData> {
        lateinit var docRef: DocumentReference
        auth.currentUser?.let { firebaseUser ->
            docRef = Firebase.firestore
                .document("$JANKEN_USER_COLLECTION/${firebaseUser.uid}")
        }
        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                lateinit var userData: UserData
                if (query.data.exists()) {
                    query.data.toObject<UserData>()?.let {
                        userData = it
                    }
                } else {
                    userData = UserData(
                        null,  auth.currentUser?.displayName!!,
                        auth.currentUser?.uid!!, 1,
                        0, 0, 0,
                        0, 0, "${auth.currentUser?.uid!!}.jpg",
                        "", "tokenIntance.token"
                    )
                }
                RepositoryResult.Success(userData)
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getTopScoreUser(): RepositoryResult<List<UserData>> {
        val docRef = Firebase.firestore
            .collection(JANKEN_USER_COLLECTION)
            .orderBy("exp")
            .limitToLast(5)

        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                val listUser = mutableListOf<UserData>()
                if (query.data.documents.isNotEmpty()) {
                    query.data.documents.forEach { docSnap ->
                        val singleData = docSnap.toObject<UserData>()
                        singleData?.let {
                            listUser.add(it)
                        }
                    }
                }
                listUser.sortByDescending { it.exp }
                RepositoryResult.Success(listUser)
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getUserDataByUid(uid: String): RepositoryResult<UserData> {
        val docRef = Firebase.firestore
            .document("$JANKEN_USER_COLLECTION/$uid")
        return when (val query = docRef.get().awaits()) {

            is RepositoryResult.Success -> {
                lateinit var userData: UserData
                if (query.data.exists()) {
                    query.data.toObject<UserData>()?.let {
                        userData = it
                    }
                } else {
                    userData = UserData(
                        null, auth.currentUser?.displayName!!,
                        auth.currentUser?.uid!!,  1,
                        0, 0, 0,
                        0, 0, "${auth.currentUser?.uid!!}.jpg",
                        "", "tokenIntance.token"
                    )
                }
                RepositoryResult.Success(userData)
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateFrameUrl(frameUrlMap: Map<String, String>): RepositoryResult<String> {
        val docRef =
            Firebase.firestore.document("/$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}")
        return when (val query = docRef.set(frameUrlMap, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update frameUrl success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateUserPoint(userDataMap: Map<String, Int>): RepositoryResult<String> {
        val docRef = Firebase.firestore
            .document("/$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}")
        return when (val query = docRef.set(userDataMap, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update frameUrl success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }
}