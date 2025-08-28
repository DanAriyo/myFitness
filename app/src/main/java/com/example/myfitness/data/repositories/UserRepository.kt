package com.example.myfitness.data.repositories

import com.example.myfitness.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class UserRepository(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User): Boolean {
        return try {
            // Usa `doc()` per specificare l'ID del documento
            usersCollection.document(user.id).set(user).await()
            Log.d("UserRepository", "Utente creato con ID: ${user.id}")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Errore creando utente: ${e.message}", e)
            false
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            Log.d("UserRepository", "Utente letto: $user , userId: $userId")
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Errore leggendo utente: ${e.message}", e)
            null
        }
    }

    // Aggiorna i dati di un utente
    suspend fun updateUser(userId: String, updatedUser: User): Boolean {
        return try {
            usersCollection.document(userId).set(updatedUser).await()
            Log.d("UserRepository", "Utente aggiornato: $userId")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Errore aggiornando utente: ${e.message}", e)
            false
        }
    }
}
