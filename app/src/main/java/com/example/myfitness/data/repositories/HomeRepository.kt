package com.example.myfitness.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class DailyProgress(
    val date: String = "",      // formato "yyyy-MM-dd"
    val steps: Int = 0,
    val km: Float = 0f,         // chilometri percorsi
    val height: Float = 0f,
    val calories: Int = 0,
    val weight: Float = 0f
)

class HomeRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    private val collection = "daily_progress"

    suspend fun saveProgress(progress: DailyProgress): Boolean {
        return try {
            val user = auth.currentUser ?: throw Exception("Utente non loggato")
            val docId = "${user.uid}_${progress.date}"
            db.collection(collection)
                .document(docId)
                .set(progress)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getProgress(date: String): DailyProgress? {
        return try {
            val user = auth.currentUser ?: throw Exception("Utente non loggato")
            val docId = "${user.uid}_$date"
            val snapshot = db.collection(collection).document(docId).get().await()
            snapshot.toObject(DailyProgress::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
