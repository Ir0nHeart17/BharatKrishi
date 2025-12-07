package com.bharatkrishi.app.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(uri: Uri): String? {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("detections/$filename.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveDetection(
        userId: String,
        diseaseName: String,
        confidence: Float,
        imageUrl: String,
        location: String?
    ) {
        val detection = hashMapOf(
            "userId" to userId,
            "diseaseName" to diseaseName,
            "confidence" to confidence,
            "imageUrl" to imageUrl,
            "location" to location,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("detections").add(detection).await()
    }
}
