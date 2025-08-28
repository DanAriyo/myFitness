package com.example.myfitness.data.repositories

import android.util.Log
import com.example.myfitness.data.models.Exercise
import com.example.myfitness.data.models.Training
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TrainingRepository(
    private val firestore: FirebaseFirestore
) {

    suspend fun createTraining(userId: String, training: Training): Boolean {
        return try {
            val userTrainingCollection = firestore
                .collection("users")
                .document(userId)
                .collection("trainings")

            val newTrainingDocRef = userTrainingCollection.document()
            val trainingId = newTrainingDocRef.id

            // Salva il documento del training
            newTrainingDocRef.set(training.copy(id = trainingId)).await()

            // Salva gli esercizi come sottocollezione con ID esplicito
            if (training.esercizi.isNotEmpty()) {
                val exercisesBatch = firestore.batch()
                training.esercizi.forEach { exercise ->
                    val exerciseDocRef = newTrainingDocRef.collection("exercises").document()
                    val exerciseId = exerciseDocRef.id

                    // Aggiorno l'esercizio con l'id prima di salvarlo
                    exercisesBatch.set(exerciseDocRef, exercise.copy(id = exerciseId))
                }
                exercisesBatch.commit().await()
            }

            Log.d("TrainingRepository", "Allenamento creato con successo per l'utente $userId con ID: $trainingId")
            true
        } catch (e: Exception) {
            Log.e("TrainingRepository", "Errore creando allenamento: ${e.message}", e)
            false
        }
    }

    suspend fun getAllTrainingsForUser(userId: String): List<Training> {
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("trainings")
                .get()
                .await()

            val trainings = snapshot.documents.mapNotNull { document ->
                document.toObject(Training::class.java)
            }
            Log.d("TrainingRepository", "Recuperati ${trainings.size} allenamenti per l'utente $userId")
            trainings
        } catch (e: Exception) {
            Log.e("TrainingRepository", "Errore nel recupero degli allenamenti: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun getTrainingById(userId: String, trainingId: String): Training? {
        return try {
            val trainingDocRef = firestore
                .collection("users").document(userId)
                .collection("trainings").document(trainingId)

            val snapshot = trainingDocRef.get().await()

            val training = snapshot.toObject(Training::class.java)

            if (training != null) {
                Log.d("TrainingRepository", "Allenamento $trainingId recuperato con successo.")
                val exercisesSnapshot = trainingDocRef.collection("exercises").get().await()
                val exercises = exercisesSnapshot.documents.mapNotNull {
                    it.toObject(Exercise::class.java)
                }
                // Restituisco l'oggetto Training completo di esercizi
                training.copy(esercizi = exercises)
            } else {
                Log.d("TrainingRepository", "Allenamento $trainingId non trovato.")
                null
            }
        } catch (e: Exception) {
            Log.e("TrainingRepository", "Errore nel recupero dell'allenamento $trainingId: ${e.message}", e)
            null
        }
    }
}