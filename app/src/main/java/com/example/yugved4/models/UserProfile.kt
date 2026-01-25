package com.example.yugved4.models

/**
 * Data class for user profile information from database
 */
data class UserProfile(
    val id: Int,
    val name: String? = null,
    val targetCalories: Int,
    val currentWeight: Double,
    val age: Int? = null,
    val gender: String? = null,
    val activityLevel: String? = null,
    val dietPreference: String? = null,
    val height: Double? = null,
    val firebaseUid: String? = null
)
