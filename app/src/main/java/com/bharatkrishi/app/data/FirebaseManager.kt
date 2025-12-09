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
        latitude: Double,
        longitude: Double,
        locationName: String?
    ) {
        val detection = hashMapOf(
            "userId" to userId,
            "diseaseName" to diseaseName,
            "confidence" to confidence,
            "imageUrl" to imageUrl,
            "latitude" to latitude,
            "longitude" to longitude,
            "locationName" to locationName,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("detections").add(detection).await()
    }
    
    suspend fun saveUserProfile(profile: UserProfile) {
        try {
            db.collection("users").document(profile.id).set(profile).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun saveCropRegistration(
        userId: String,
        cropName: String,
        area: String,
        imageUrl: String,
        location: String
    ) {
        val registration = hashMapOf(
            "userId" to userId,
            "cropName" to cropName,
            "area" to area,
            "imageUrl" to imageUrl,
            "location" to location,
            "timestamp" to System.currentTimeMillis(),
            "status" to "Pending Verification"
        )
        db.collection("crop_registrations").add(registration).await()
    }

    suspend fun saveDisasterReport(
        userId: String,
        damageType: String,
        description: String,
        imageUrl: String,
        location: String
    ) {
        val report = hashMapOf(
            "userId" to userId,
            "damageType" to damageType,
            "description" to description,
            "imageUrl" to imageUrl,
            "location" to location,
            "timestamp" to System.currentTimeMillis(),
            "status" to "Under Review"
        )
        db.collection("disaster_reports").add(report).await()
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val farmSize: String = "",
    val mainCrops: String = "",
    val soilType: String = "",
    val village: String = "",
    val district: String = "",
    val state: String = "",
    val experience: String = ""
)
