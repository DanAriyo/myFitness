package com.example.myfitness.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    private var lastCreatedUserId: String? = null

    suspend fun register(email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            lastCreatedUserId = result.user?.uid;
            Log.d("AuthRepository", "lastCreatedUserId = $result")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Errore durante la registrazione: ${e.message}", e)
            false
        }
    }

    fun getLastCreatedUserId(): String {
        Log.d("AuthRepository", "getLastCreatedUserId() restituisce: $lastCreatedUserId")
        return lastCreatedUserId ?: ""
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }





    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUserEmail(): String? = auth.currentUser?.email
}
