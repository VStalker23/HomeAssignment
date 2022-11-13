package com.vladimirorlov.homeassignment.model

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseManager private constructor(val context: Context) {

    val db = Firebase.firestore

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: FirebaseManager

        fun getInstance(context: Context): FirebaseManager {
            if (!Companion::instance.isInitialized)
                instance = FirebaseManager(context)
            return instance
        }
    }

    fun addContactToDatabase(contact: Contact): Task<Void> {
        return db.collection("contacts").document(contact.phoneNumber).set(contact)
    }
}