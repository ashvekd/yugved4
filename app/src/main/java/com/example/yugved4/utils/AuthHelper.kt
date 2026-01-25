package com.example.yugved4.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Helper class for Firebase Authentication operations
 */
object AuthHelper {
    
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    
    /**
     * Get the currently signed-in user
     * @return FirebaseUser if signed in, null otherwise
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    /**
     * Check if a user is currently signed in
     * @return true if user is signed in, false otherwise
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Get the current user's UID
     * @return User ID string or null if not signed in
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Get the current user's email
     * @return Email string or null if not signed in
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
    
    /**
     * Get the current user's display name
     * @return Display name string or null if not signed in
     */
    fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }
}
