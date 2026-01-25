package com.example.yugved4.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Helper class for Firebase Firestore operations
 */
object FirestoreHelper {
    
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private const val COLLECTION_USERS = "users"
    
    /**
     * Save user profile to Firestore
     * @param uid Firebase user ID
     * @param userData Map of user data to save
     */
    suspend fun saveUserProfile(uid: String, userData: Map<String, Any>) {
        try {
            db.collection(COLLECTION_USERS)
                .document(uid)
                .set(userData)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    /**
     * Get user profile from Firestore
     * @param uid Firebase user ID
     * @return Map of user data or null if not found
     */
    suspend fun getUserProfile(uid: String): Map<String, Any>? {
        return try {
            val document = db.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                document.data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Check if user profile exists in Firestore
     * @param uid Firebase user ID
     * @return true if profile exists, false otherwise
     */
    suspend fun hasUserProfile(uid: String): Boolean {
        return try {
            val document = db.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .await()
            document.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Update specific fields in user profile
     * @param uid Firebase user ID
     * @param updates Map of fields to update
     */
    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>) {
        try {
            db.collection(COLLECTION_USERS)
                .document(uid)
                .update(updates)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
