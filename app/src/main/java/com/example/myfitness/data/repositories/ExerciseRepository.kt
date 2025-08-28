package com.example.myfitness.data.repositories

import com.example.myfitness.data.models.Exercise
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class ExerciseRepository(
    private val firestore: FirebaseFirestore
) {

    private val exercisesCollection = firestore.collection("exercises")

    suspend fun getExercises(): List<Exercise> {
        return try {
            val snapshot = exercisesCollection.get().await()
            val exercises = snapshot.documents.mapNotNull { it.toObject(Exercise::class.java) }
            Log.d("ExerciseRepository", "Esercizi letti con successo: ${exercises.size}")
            exercises
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Errore nella lettura degli esercizi: ${e.message}", e)
            emptyList()
        }
    }
}
