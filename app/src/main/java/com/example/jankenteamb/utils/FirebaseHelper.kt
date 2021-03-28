package com.example.jankenteamb.utils

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference


class FirebaseHelper(private val auth: FirebaseAuth, private val mStorageRef : StorageReference ) {

    fun isFirebaseLogin(): Boolean {
        return auth.currentUser != null
    }

    fun signoutFirebase() {
        auth.signOut()
    }

    fun getUsername(): String {
        return auth.currentUser?.displayName!!
    }

    fun getProfilePhotoUri(): Uri {
        return auth.currentUser?.photoUrl!!
    }





}