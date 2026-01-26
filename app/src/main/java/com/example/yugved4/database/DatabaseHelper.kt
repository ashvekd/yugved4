package com.example.yugved4.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.yugved4.models.Doctor
import com.example.yugved4.models.UserProfile
import com.example.yugved4.utils.AuthHelper
import com.example.yugved4.utils.FirestoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SQLiteOpenHelper for Doctor Database
 * Uses raw SQL statements for database operations
 */
class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "yugved.db"
        private const val DATABASE_VERSION = 12  // Incremented for video support
        
        // Doctors table and columns
        private const val TABLE_DOCTORS = "doctors"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_SPECIALTY = "specialty"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        
        // User profile table and columns
        private const val TABLE_USER_PROFILE = "user_profile"
        private const val COLUMN_PROFILE_ID = "id"
        private const val COLUMN_PROFILE_NAME = "name"
        private const val COLUMN_TARGET_CALORIES = "target_calories"
        private const val COLUMN_CURRENT_WEIGHT = "current_weight"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_ACTIVITY_LEVEL = "activity_level"
        private const val COLUMN_DIET_PREFERENCE = "diet_preference"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_FIREBASE_UID = "firebase_uid"
        
        // Mental health content table and columns
        private const val TABLE_MENTAL_HEALTH = "mental_health_content"
        private const val COLUMN_MH_ID = "id"
        private const val COLUMN_MH_TITLE = "title"
        private const val COLUMN_MH_TYPE = "type"  // Video/Tip
        private const val COLUMN_MH_CATEGORY = "category"  // Meditation/Stress/Sleep
        private const val COLUMN_MH_CONTENT_DATA = "content_data"  // Link or Description
        
        // Helplines table and columns
        private const val TABLE_HELPLINES = "helplines"
        private const val COLUMN_HELPLINE_ID = "id"
        private const val COLUMN_HELPLINE_NAME = "name"
        private const val COLUMN_HELPLINE_NUMBER = "number"
        private const val COLUMN_HELPLINE_DESCRIPTION = "description"
        
        // Muscle groups table and columns
        private const val TABLE_MUSCLE_GROUPS = "muscle_groups"
        private const val COLUMN_MUSCLE_ID = "muscle_id"
        private const val COLUMN_MUSCLE_NAME = "muscle_name"
        private const val COLUMN_MUSCLE_IMAGE = "muscle_image_resource"
        
        // Gym exercises table and columns
        private const val TABLE_GYM_EXERCISES = "gym_exercises"
        private const val COLUMN_EXERCISE_ID = "exercise_id"
        private const val COLUMN_EXERCISE_MUSCLE_ID = "muscle_id"
        private const val COLUMN_EXERCISE_NAME = "exercise_name"
        private const val COLUMN_EXERCISE_DESCRIPTION = "exercise_description"
        private const val COLUMN_EXERCISE_THUMBNAIL = "thumbnail_resource"
        private const val COLUMN_EXERCISE_VIDEO_URL = "video_url"
        
        // Yoga asanas table and columns
        private const val TABLE_YOGA_ASANAS = "yoga_asanas"
        private const val COLUMN_ASANA_ID = "asana_id"
        private const val COLUMN_ASANA_NAME = "asana_name"
        private const val COLUMN_SANSKRIT_NAME = "sanskrit_name"
        private const val COLUMN_ASANA_DESCRIPTION = "description"
        private const val COLUMN_ASANA_BENEFITS = "benefits"  // Comma-separated
        private const val COLUMN_ASANA_DURATION = "duration"
        private const val COLUMN_ASANA_DIFFICULTY = "difficulty_level"
        private const val COLUMN_ASANA_CATEGORY = "category"
        private const val COLUMN_ASANA_THUMBNAIL = "thumbnail_resource"
        private const val COLUMN_ASANA_VIDEO_URL = "video_url"
        
        // Meal plans table and columns
        private const val TABLE_MEAL_PLANS = "meal_plans"
        private const val COLUMN_PLAN_ID = "plan_id"
        private const val COLUMN_MIN_CALORIES = "min_calories"
        private const val COLUMN_MAX_CALORIES = "max_calories"
        private const val COLUMN_DIET_TYPE = "diet_type"  // 'Veg' or 'Non-Veg'
        private const val COLUMN_BREAKFAST = "breakfast"
        private const val COLUMN_LUNCH = "lunch"
        private const val COLUMN_DINNER = "dinner"
        private const val COLUMN_SNACKS = "snacks"
        
        // App settings table and columns
        private const val TABLE_APP_SETTINGS = "app_settings"
        private const val COLUMN_SETTING_KEY = "key"
        private const val COLUMN_SETTING_VALUE = "value"
        
        // Daily steps table and columns
        private const val TABLE_DAILY_STEPS = "daily_steps"
        private const val COLUMN_STEP_DATE = "date"
        private const val COLUMN_STEP_COUNT = "step_count"
        
        // Daily moods table and columns
        private const val TABLE_DAILY_MOODS = "daily_moods"
        private const val COLUMN_MOOD_DATE = "date"
        private const val COLUMN_MOOD_SCORE = "mood_score"
        
        // Peace quotes table and columns
        private const val TABLE_PEACE_QUOTES = "peace_quotes"
        private const val COLUMN_QUOTE_ID = "id"
        private const val COLUMN_QUOTE_TEXT = "quote_text"
    }

    /**
     * Create database table
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Create doctors table
        val createDoctorsTable = """
            CREATE TABLE $TABLE_DOCTORS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_SPECIALTY TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT
            )
        """.trimIndent()
        
        // Create user profile table
        val createProfileTable = """
            CREATE TABLE $TABLE_USER_PROFILE (
                $COLUMN_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PROFILE_NAME TEXT,
                $COLUMN_TARGET_CALORIES INTEGER NOT NULL,
                $COLUMN_CURRENT_WEIGHT REAL NOT NULL,
                $COLUMN_AGE INTEGER,
                $COLUMN_GENDER TEXT,
                $COLUMN_ACTIVITY_LEVEL TEXT,
                $COLUMN_DIET_PREFERENCE TEXT,
                $COLUMN_HEIGHT REAL,
                $COLUMN_FIREBASE_UID TEXT
            )
        """.trimIndent()
        
        // Create mental health content table
        val createMentalHealthTable = """
            CREATE TABLE $TABLE_MENTAL_HEALTH (
                $COLUMN_MH_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MH_TITLE TEXT NOT NULL,
                $COLUMN_MH_TYPE TEXT NOT NULL,
                $COLUMN_MH_CATEGORY TEXT NOT NULL,
                $COLUMN_MH_CONTENT_DATA TEXT NOT NULL
            )
        """.trimIndent()
        
        // Create helplines table
        val createHelplinesTable = """
            CREATE TABLE $TABLE_HELPLINES (
                $COLUMN_HELPLINE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HELPLINE_NAME TEXT NOT NULL,
                $COLUMN_HELPLINE_NUMBER TEXT NOT NULL,
                $COLUMN_HELPLINE_DESCRIPTION TEXT NOT NULL
            )
        """.trimIndent()
        
        // Create muscle groups table
        val createMuscleGroupsTable = """
            CREATE TABLE $TABLE_MUSCLE_GROUPS (
                $COLUMN_MUSCLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MUSCLE_NAME TEXT NOT NULL,
                $COLUMN_MUSCLE_IMAGE INTEGER NOT NULL
            )
        """.trimIndent()
        
        // Create gym exercises table
        val createGymExercisesTable = """
            CREATE TABLE $TABLE_GYM_EXERCISES (
                $COLUMN_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXERCISE_MUSCLE_ID INTEGER NOT NULL,
                $COLUMN_EXERCISE_NAME TEXT NOT NULL,
                $COLUMN_EXERCISE_DESCRIPTION TEXT NOT NULL,
                $COLUMN_EXERCISE_THUMBNAIL INTEGER NOT NULL,
                $COLUMN_EXERCISE_VIDEO_URL TEXT,
                FOREIGN KEY($COLUMN_EXERCISE_MUSCLE_ID) REFERENCES $TABLE_MUSCLE_GROUPS($COLUMN_MUSCLE_ID)
            )
        """.trimIndent()
        
        db.execSQL(createDoctorsTable)
        db.execSQL(createProfileTable)
        db.execSQL(createMentalHealthTable)
        db.execSQL(createHelplinesTable)
        db.execSQL(createMuscleGroupsTable)
        db.execSQL(createGymExercisesTable)
        
        // Insert seed data for mental health content
        insertMentalHealthSeedData(db)
        
        // Insert seed data for helplines
        insertHelplinesSeedData(db)
        
        // Create yoga asanas table
        val createYogaAsanasTable = """
            CREATE TABLE $TABLE_YOGA_ASANAS (
                $COLUMN_ASANA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ASANA_NAME TEXT NOT NULL,
                $COLUMN_SANSKRIT_NAME TEXT NOT NULL,
                $COLUMN_ASANA_DESCRIPTION TEXT NOT NULL,
                $COLUMN_ASANA_BENEFITS TEXT NOT NULL,
                $COLUMN_ASANA_DURATION TEXT NOT NULL,
                $COLUMN_ASANA_DIFFICULTY TEXT NOT NULL,
                $COLUMN_ASANA_CATEGORY TEXT NOT NULL,
                $COLUMN_ASANA_THUMBNAIL INTEGER NOT NULL,
                $COLUMN_ASANA_VIDEO_URL TEXT
            )
        """.trimIndent()
        db.execSQL(createYogaAsanasTable)
        
        // Create meal plans table
        val createMealPlansTable = """
            CREATE TABLE $TABLE_MEAL_PLANS (
                $COLUMN_PLAN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MIN_CALORIES INTEGER NOT NULL,
                $COLUMN_MAX_CALORIES INTEGER NOT NULL,
                $COLUMN_DIET_TYPE TEXT NOT NULL,
                $COLUMN_BREAKFAST TEXT NOT NULL,
                $COLUMN_LUNCH TEXT NOT NULL,
                $COLUMN_DINNER TEXT NOT NULL,
                $COLUMN_SNACKS TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createMealPlansTable)
        
        // Insert seed data for muscle groups and exercises
        insertMuscleGroupsSeedData(db)
        insertGymExercisesSeedData(db)
        
        // Insert seed data for yoga asanas
        insertYogaAsanasSeedData(db)
        
        // Insert seed data for meal plans
        insertMealPlansSeedData(db)
        
        // Create app settings table
        val createAppSettingsTable = """
            CREATE TABLE $TABLE_APP_SETTINGS (
                $COLUMN_SETTING_KEY TEXT PRIMARY KEY,
                $COLUMN_SETTING_VALUE INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createAppSettingsTable)
        
        // Create daily steps table
        val createDailyStepsTable = """
            CREATE TABLE $TABLE_DAILY_STEPS (
                $COLUMN_STEP_DATE TEXT PRIMARY KEY,
                $COLUMN_STEP_COUNT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createDailyStepsTable)
        
        // Create daily moods table
        val createDailyMoodsTable = """
            CREATE TABLE $TABLE_DAILY_MOODS (
                $COLUMN_MOOD_DATE TEXT PRIMARY KEY,
                $COLUMN_MOOD_SCORE INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createDailyMoodsTable)
        
        // Create peace quotes table
        val createPeaceQuotesTable = """
            CREATE TABLE $TABLE_PEACE_QUOTES (
                $COLUMN_QUOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QUOTE_TEXT TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createPeaceQuotesTable)
        
        // Insert seed data for peace quotes
        insertPeaceQuotesSeedData(db)
    }

    /**
     * Upgrade database (drop and recreate)
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOCTORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_PROFILE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MENTAL_HEALTH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HELPLINES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MUSCLE_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GYM_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_YOGA_ASANAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEAL_PLANS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APP_SETTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DAILY_STEPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DAILY_MOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PEACE_QUOTES")
        onCreate(db)
    }

    /**
     * Add a new doctor to database
     * @return row ID of newly inserted doctor, or -1 if error
     */
    fun addDoctor(name: String, specialty: String, phone: String, email: String? = null): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_SPECIALTY, specialty)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }
        
        val id = db.insert(TABLE_DOCTORS, null, values)
        db.close()
        
        // Sync to Firestore in background
        if (id != -1L) {
            val doctor = Doctor(
                id = id.toString(),
                name = name,
                specialty = specialty,
                phoneNumber = phone,
                email = email
            )
            val uid = AuthHelper.getCurrentUser()?.uid
            if (uid != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    FirestoreHelper.syncDoctorToFirestore(uid, doctor)
                }
            }
        }
        
        return id
    }

    /**
     * Get all doctors from database using raw SQL query
     * @return List of Doctor objects
     */
    fun getAllDoctors(): List<Doctor> {
        val doctorList = mutableListOf<Doctor>()
        val db = readableDatabase
        
        db.rawQuery("SELECT * FROM $TABLE_DOCTORS", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    val specialty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIALTY))
                    val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
                    val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                    
                    val doctor = Doctor(
                        id = id.toString(),
                        name = name,
                        specialty = specialty,
                        phoneNumber = phone,
                        email = email
                    )
                    doctorList.add(doctor)
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return doctorList
    }

    /**
     * Update an existing doctor
     * @return number of rows affected
     */
    fun updateDoctor(id: String, name: String, specialty: String, phone: String, email: String? = null): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_SPECIALTY, specialty)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }
        
        val rowsAffected = db.update(TABLE_DOCTORS, values, "$COLUMN_ID = ?", arrayOf(id))
        db.close()
        
        // Sync to Firestore in background
        if (rowsAffected > 0) {
            val doctor = Doctor(
                id = id,
                name = name,
                specialty = specialty,
                phoneNumber = phone,
                email = email
            )
            val uid = AuthHelper.getCurrentUser()?.uid
            if (uid != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    FirestoreHelper.syncDoctorToFirestore(uid, doctor)
                }
            }
        }
        
        return rowsAffected
    }

    /**
     * Delete a doctor from database
     * @return number of rows deleted
     */
    fun deleteDoctor(id: String): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_DOCTORS, "$COLUMN_ID = ?", arrayOf(id))
        db.close()
        
        // Delete from Firestore in background
        if (rowsDeleted > 0) {
            val uid = AuthHelper.getCurrentUser()?.uid
            if (uid != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    FirestoreHelper.deleteDoctorFromFirestore(uid, id)
                }
            }
        }
        
        return rowsDeleted
    }

    /**
     * Get doctor count
     */
    fun getDoctorCount(): Int {
        val db = readableDatabase
        var count = 0
        
        db.rawQuery("SELECT COUNT(*) FROM $TABLE_DOCTORS", null).use { cursor ->
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
        }
        
        db.close()
        return count
    }

    /**
     * Delete all doctors
     */
    fun deleteAllDoctors(): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_DOCTORS, null, null)
        db.close()
        return rowsDeleted
    }
    
    // =============== USER PROFILE METHODS ===============
    
    /**
     * Save user profile (diet data)
     * @return row ID or -1 if error
     */
    fun saveUserProfile(
        targetCalories: Int,
        currentWeight: Double,
        age: Int? = null,
        gender: String? = null,
        activityLevel: String? = null,
        dietPreference: String? = null,
        height: Double? = null,
        name: String? = null,
        firebaseUid: String? = null
    ): Long {
        val db = writableDatabase
        
        // Delete existing profile (single user app)
        db.delete(TABLE_USER_PROFILE, null, null)
        
        val values = ContentValues().apply {
            put(COLUMN_PROFILE_NAME, name)
            put(COLUMN_TARGET_CALORIES, targetCalories)
            put(COLUMN_CURRENT_WEIGHT, currentWeight)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_ACTIVITY_LEVEL, activityLevel)
            put(COLUMN_DIET_PREFERENCE, dietPreference)
            put(COLUMN_HEIGHT, height)
            put(COLUMN_FIREBASE_UID, firebaseUid)
        }
        
        val id = db.insert(TABLE_USER_PROFILE, null, values)
        db.close()
        
        // Sync to Firestore in background
        val profile = getUserProfile()
        if (profile != null) {
            val uid = AuthHelper.getCurrentUser()?.uid
            if (uid != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    FirestoreHelper.syncUserProfileToFirestore(uid, profile)
                }
            }
        }
        
        return id
    }
    
    /**
     * Get user profile from database
     * @return UserProfile object or null if not found
     */
    fun getUserProfile(): UserProfile? {
        val db = readableDatabase
        var profile: UserProfile? = null
        
        db.rawQuery("SELECT * FROM $TABLE_USER_PROFILE LIMIT 1", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_NAME))
                val targetCalories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TARGET_CALORIES))
                val currentWeight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_WEIGHT))
                val age = if (!cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_AGE))) {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE))
                } else null
                val gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
                val activityLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_LEVEL))
                val dietPreference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIET_PREFERENCE))
                val height = if (!cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT))) {
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT))
                } else null
                val firebaseUid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIREBASE_UID))
                
                profile = UserProfile(
                    id = id,
                    name = name,
                    targetCalories = targetCalories,
                    currentWeight = currentWeight,
                    age = age,
                    gender = gender,
                    activityLevel = activityLevel,
                    dietPreference = dietPreference,
                    height = height,
                    firebaseUid = firebaseUid
                )
            }
        }
        
        db.close()
        return profile
    }
    
    /**
     * Check if user profile exists
     * @return true if profile exists, false otherwise
     */
    fun hasUserProfile(): Boolean {
        val db = readableDatabase
        var hasProfile = false
        
        db.rawQuery("SELECT COUNT(*) FROM $TABLE_USER_PROFILE", null).use { cursor ->
            if (cursor.moveToFirst()) {
                hasProfile = cursor.getInt(0) > 0
            }
        }
        
        db.close()
        return hasProfile
    }
    
    /**
     * Load user profile from Firestore to local database
     * Used when app is reinstalled and local database is empty
     * @return true if profile was loaded successfully, false otherwise
     */
    suspend fun loadProfileFromCloud(): Boolean {
        val uid = AuthHelper.getCurrentUser()?.uid ?: return false
        
        try {
            val cloudProfile = FirestoreHelper.loadUserProfileFromFirestore(uid)
            
            if (cloudProfile != null) {
                // Save to local database
                saveUserProfile(
                    targetCalories = cloudProfile.targetCalories,
                    currentWeight = cloudProfile.currentWeight,
                    age = cloudProfile.age,
                    gender = cloudProfile.gender,
                    activityLevel = cloudProfile.activityLevel,
                    dietPreference = cloudProfile.dietPreference,
                    height = cloudProfile.height,
                    name = cloudProfile.name,
                    firebaseUid = cloudProfile.firebaseUid
                )
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return false
    }
    
    /**
     * Load doctors from Firestore to local database
     * Used when app is reinstalled and local database is empty
     * @return Number of doctors loaded
     */
    suspend fun loadDoctorsFromCloud(): Int {
        val uid = AuthHelper.getCurrentUser()?.uid ?: return 0
        
        try {
            val cloudDoctors = FirestoreHelper.loadDoctorsFromFirestore(uid)
            
            // Clear existing doctors first
            deleteAllDoctors()
            
            // Insert each doctor (without triggering sync back to cloud)
            val db = writableDatabase
            var count = 0
            
            for (doctor in cloudDoctors) {
                val values = ContentValues().apply {
                    put(COLUMN_NAME, doctor.name)
                    put(COLUMN_SPECIALTY, doctor.specialty)
                    put(COLUMN_PHONE, doctor.phoneNumber)
                    put(COLUMN_EMAIL, doctor.email)
                }
                
                val id = db.insert(TABLE_DOCTORS, null, values)
                if (id != -1L) count++
            }
            
            db.close()
            return count
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }
    
    /**
     * Update user weight
     */
    fun updateWeight(newWeight: Double): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CURRENT_WEIGHT, newWeight)
        }
        
        val rowsAffected = db.update(TABLE_USER_PROFILE, values, null, null)
        db.close()
        return rowsAffected
    }
    
    /**
     * Update user profile with all editable fields
     * @return number of rows affected
     */
    fun updateUserProfile(
        name: String?,
        age: Int,
        height: Double,
        weight: Double
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PROFILE_NAME, name)
            put(COLUMN_AGE, age)
            put(COLUMN_HEIGHT, height)
            put(COLUMN_CURRENT_WEIGHT, weight)
        }
        
        val rowsAffected = db.update(TABLE_USER_PROFILE, values, null, null)
        db.close()
        return rowsAffected
    }
    
    /**
     * Calculate BMR (Basal Metabolic Rate) using Mifflin-St Jeor formula
     * @param age User's age in years
     * @param gender User's gender ("Male" or "Female")
     * @param height User's height in cm
     * @param weight User's weight in kg
     * @return BMR in calories
     */
    private fun calculateBMR(age: Int, gender: String?, height: Double, weight: Double): Double {
        return if (gender == "Male") {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }
    }
    
    /**
     * Update target calories based on current profile data
     * Recalculates BMR and applies activity level multiplier
     * @return number of rows affected
     */
    fun updateTargetCalories(): Int {
        val profile = getUserProfile() ?: return 0
        
        // Ensure we have necessary data
        val age = profile.age ?: return 0
        val height = profile.height ?: return 0
        val weight = profile.currentWeight
        val gender = profile.gender
        val activityLevel = profile.activityLevel ?: "Sedentary"
        
        // Calculate BMR
        val bmr = calculateBMR(age, gender, height, weight)
        
        // Activity level multipliers
        val multiplier = when {
            activityLevel.contains("Sedentary", ignoreCase = true) -> 1.2
            activityLevel.contains("Lightly", ignoreCase = true) -> 1.375
            activityLevel.contains("Moderately", ignoreCase = true) -> 1.55
            activityLevel.contains("Very", ignoreCase = true) -> 1.725
            activityLevel.contains("Extra", ignoreCase = true) -> 1.9
            else -> 1.2
        }
        
        val targetCalories = (bmr * multiplier).toInt()
        
        // Update target calories in database
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TARGET_CALORIES, targetCalories)
        }
        
        val rowsAffected = db.update(TABLE_USER_PROFILE, values, null, null)
        db.close()
        return rowsAffected
    }

    // ===========================
    // Workout Statistics
    // ===========================
    
    /**
     * Get workout statistics (counts of gym exercises and yoga asanas)
     * 
     * NOTE: Currently, gym exercises and yoga asanas are stored in DataProviders
     * (static code), not in SQL tables. This method returns counts from those sources.
     * If you want to store exercises in SQL, you'll need to create tables and migrate the data.
     * 
     * @return WorkoutStats object with gym and yoga counts
     */
    fun getWorkoutStats(): com.example.yugved4.models.WorkoutStats {
        // Gym exercises count (from GymFragment hardcoded categories)
        // Categories: Chest(12), Back(10), Legs(15), Shoulders(8), Arms(14), Core(11)
        val gymExerciseCount = 12 + 10 + 15 + 8 + 14 + 11 // Total: 70 exercises
        
        // Yoga asanas count (from YogaDataProvider)
        val yogaAsanaCount = com.example.yugved4.utils.YogaDataProvider.getAllAsanas().size
        
        return com.example.yugved4.models.WorkoutStats(
            gymExerciseCount = gymExerciseCount,
            yogaAsanaCount = yogaAsanaCount
        )
    }
    
    // =============== MENTAL HEALTH CONTENT METHODS ===============
    
    /**
     * Insert seed data for mental health content
     * This method is called during database creation
     */
    private fun insertMentalHealthSeedData(db: SQLiteDatabase) {
        // Sample record 1: 5-Minute Breathing meditation video
        db.execSQL("""
            INSERT INTO $TABLE_MENTAL_HEALTH 
            ($COLUMN_MH_TITLE, $COLUMN_MH_TYPE, $COLUMN_MH_CATEGORY, $COLUMN_MH_CONTENT_DATA) 
            VALUES 
            ('5-Minute Breathing Exercise', 'Video', 'Meditation', 'https://www.youtube.com/watch?v=tybOi4hjZFQ')
        """)
        
        // Sample record 2: Better Sleep tip
        db.execSQL("""
            INSERT INTO $TABLE_MENTAL_HEALTH 
            ($COLUMN_MH_TITLE, $COLUMN_MH_TYPE, $COLUMN_MH_CATEGORY, $COLUMN_MH_CONTENT_DATA) 
            VALUES 
            ('Better Sleep Tips', 'Tip', 'Sleep', 'Maintain a consistent sleep schedule by going to bed and waking up at the same time every day. Avoid screens 1 hour before bedtime and create a relaxing bedtime routine.')
        """)
        
        // Sample record 3: Stress Relief guide
        db.execSQL("""
            INSERT INTO $TABLE_MENTAL_HEALTH 
            ($COLUMN_MH_TITLE, $COLUMN_MH_TYPE, $COLUMN_MH_CATEGORY, $COLUMN_MH_CONTENT_DATA) 
            VALUES 
            ('Stress Relief Guide', 'Tip', 'Stress', 'Practice deep breathing: Inhale for 4 counts, hold for 4, exhale for 4. Take short walks in nature. Journal your thoughts and feelings. Limit caffeine and prioritize self-care activities.')
        """)
    }
    
    /**
     * Get mental health content by category using Cursor
     * @param category The category to filter by (Meditation/Stress/Sleep)
     * @return List of MentalHealthContent objects
     */
    fun getMentalHealthContent(category: String): List<MentalHealthContent> {
        val contentList = mutableListOf<MentalHealthContent>()
        val db = readableDatabase
        
        // Raw SQL query with WHERE clause
        val query = "SELECT * FROM $TABLE_MENTAL_HEALTH WHERE $COLUMN_MH_CATEGORY = ?"
        
        db.rawQuery(query, arrayOf(category)).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MH_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_TITLE))
                    val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_TYPE))
                    val contentCategory = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_CATEGORY))
                    val contentData = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_CONTENT_DATA))
                    
                    val content = MentalHealthContent(
                        id = id,
                        title = title,
                        type = type,
                        category = contentCategory,
                        contentData = contentData
                    )
                    contentList.add(content)
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return contentList
    }
    
    /**
     * Get all mental health content (no filter)
     * @return List of all MentalHealthContent objects
     */
    fun getAllMentalHealthContent(): List<MentalHealthContent> {
        val contentList = mutableListOf<MentalHealthContent>()
        val db = readableDatabase
        
        db.rawQuery("SELECT * FROM $TABLE_MENTAL_HEALTH", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MH_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_TITLE))
                    val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_TYPE))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_CATEGORY))
                    val contentData = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MH_CONTENT_DATA))
                    
                    val content = MentalHealthContent(
                        id = id,
                        title = title,
                        type = type,
                        category = category,
                        contentData = contentData
                    )
                    contentList.add(content)
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return contentList
    }
    
    /**
     * Data class for Mental Health Content
     */
    data class MentalHealthContent(
        val id: Int,
        val title: String,
        val type: String,  // Video or Tip
        val category: String,  // Meditation, Stress, Sleep
        val contentData: String  // Link or Description
    )
    
    // =============== HELPLINES METHODS ===============
    
    /**
     * Insert seed data for helplines
     * This method is called during database creation
     */
    private fun insertHelplinesSeedData(db: SQLiteDatabase) {
        // AASRA Helpline
        db.execSQL("""
            INSERT INTO $TABLE_HELPLINES 
            ($COLUMN_HELPLINE_NAME, $COLUMN_HELPLINE_NUMBER, $COLUMN_HELPLINE_DESCRIPTION) 
            VALUES 
            ('AASRA', '9820466726', '24x7 helpline for emotional support and suicide prevention')
        """)
        
        // iCall Helpline
        db.execSQL("""
            INSERT INTO $TABLE_HELPLINES 
            ($COLUMN_HELPLINE_NAME, $COLUMN_HELPLINE_NUMBER, $COLUMN_HELPLINE_DESCRIPTION) 
            VALUES 
            ('iCall', '9152987821', 'Psychosocial helpline by TISS for mental health support')
        """)
        
        // Vandrevala Foundation
        db.execSQL("""
            INSERT INTO $TABLE_HELPLINES 
            ($COLUMN_HELPLINE_NAME, $COLUMN_HELPLINE_NUMBER, $COLUMN_HELPLINE_DESCRIPTION) 
            VALUES 
            ('Vandrevala Foundation', '9999776555', 'Mental health support and counseling services')
        """)
    }
    
    /**
     * Get all helplines from database using raw SQL query
     * @return List of Helpline objects
     */
    fun getAllHelplines(): List<HelplineData> {
        val helplineList = mutableListOf<HelplineData>()
        val db = readableDatabase
        
        db.rawQuery("SELECT * FROM $TABLE_HELPLINES", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HELPLINE_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HELPLINE_NAME))
                    val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HELPLINE_NUMBER))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HELPLINE_DESCRIPTION))
                    
                    val helpline = HelplineData(
                        id = id,
                        name = name,
                        number = number,
                        description = description
                    )
                    helplineList.add(helpline)
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return helplineList
    }
    
    /**
     * Data class for Helpline
     */
    data class HelplineData(
        val id: Int,
        val name: String,
        val number: String,
        val description: String
    )
    
    // =============== DAILY MOODS \u0026 PEACE QUOTES METHODS ===============
    
    /**
     * Insert seed data for peace quotes
     * This method is called during database creation
     */
    private fun insertPeaceQuotesSeedData(db: SQLiteDatabase) {
        val quotes = listOf(
            "Peace comes from within. Do not seek it without.",
            "The mind is everything. What you think you become.",
            "Be kind whenever possible. It is always possible.",
            "Every day may not be good, but there is something good in every day.",
            "Your mental health is a priority. Your happiness is essential. Your self-care is a necessity.",
            "You are stronger than you think. More capable than you know.",
            "Healing takes time, and asking for help is a courageous step.",
            "Small steps in the right direction can turn out to be the biggest step of your life.",
            "You don't have to be positive all the time. It's perfectly okay to feel sad, angry, or frustrated.",
            "Taking care of yourself doesn't mean me first, it means me too.",
            "Your present circumstances don't determine where you go; they merely determine where you start.",
            "Be gentle with yourself. You're doing the best you can."
        )
        
        for (quote in quotes) {
            db.execSQL("""
                INSERT INTO $TABLE_PEACE_QUOTES ($COLUMN_QUOTE_TEXT) 
                VALUES (?)
            """, arrayOf(quote))
        }
    }
    
    /**
     * Save daily mood score (1-5) for current date
     * Uses INSERT OR REPLACE to update if mood already exists for today
     * @param score Mood score from 1 (worst) to 5 (best)
     * @return true if successful, false otherwise
     */
    fun saveDailyMood(score: Int): Boolean {
        if (score !in 1..5) {
            return false
        }
        
        val db = writableDatabase
        
        // Get current date in yyyy-MM-dd format
        val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            .format(java.util.Date())
        
        val values = ContentValues().apply {
            put(COLUMN_MOOD_DATE, currentDate)
            put(COLUMN_MOOD_SCORE, score)
        }
        
        // INSERT OR REPLACE will update if date already exists
        val result = db.insertWithOnConflict(
            TABLE_DAILY_MOODS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        
        db.close()
        return result != -1L
    }
    
    /**
     * Get a random peace quote from database
     * @return Random quote string, or default message if no quotes available
     */
    fun getRandomQuote(): String {
        val db = readableDatabase
        var quote = "Take a deep breath. You are doing great."
        
        db.rawQuery(
            "SELECT $COLUMN_QUOTE_TEXT FROM $TABLE_PEACE_QUOTES ORDER BY RANDOM() LIMIT 1",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                quote = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUOTE_TEXT))
            }
        }
        
        db.close()
        return quote
    }
    
    // =============== MUSCLE GROUPS & GYM EXERCISES METHODS ===============
    
    /**
     * Insert seed data for muscle groups
     * This method is called during database creation
     */
    private fun insertMuscleGroupsSeedData(db: SQLiteDatabase) {
        // Note: Using placeholder icon resource ID (R.drawable.ic_activity)
        // Replace with actual muscle group icons later
        val iconPlaceholder = com.example.yugved4.R.drawable.ic_activity
        
        // Chest
        db.execSQL("""
            INSERT INTO $TABLE_MUSCLE_GROUPS 
            ($COLUMN_MUSCLE_NAME, $COLUMN_MUSCLE_IMAGE) 
            VALUES ('Chest', $iconPlaceholder)
        """)
        
        // Back
        db.execSQL("""
            INSERT INTO $TABLE_MUSCLE_GROUPS 
            ($COLUMN_MUSCLE_NAME, $COLUMN_MUSCLE_IMAGE) 
            VALUES ('Back', $iconPlaceholder)
        """)
        
        // Shoulders
        db.execSQL("""
            INSERT INTO $TABLE_MUSCLE_GROUPS 
            ($COLUMN_MUSCLE_NAME, $COLUMN_MUSCLE_IMAGE) 
            VALUES ('Shoulders', $iconPlaceholder)
        """)
        
        // Arms
        db.execSQL("""
            INSERT INTO $TABLE_MUSCLE_GROUPS 
            ($COLUMN_MUSCLE_NAME, $COLUMN_MUSCLE_IMAGE) 
            VALUES ('Arms', $iconPlaceholder)
        """)
        
        // Legs
        db.execSQL("""
            INSERT INTO $TABLE_MUSCLE_GROUPS 
            ($COLUMN_MUSCLE_NAME, $COLUMN_MUSCLE_IMAGE) 
            VALUES ('Legs', $iconPlaceholder)
        """)
        
        // Core
        db.execSQL("""
            INSERT INTO $TABLE_MUSCLE_GROUPS 
            ($COLUMN_MUSCLE_NAME, $COLUMN_MUSCLE_IMAGE) 
            VALUES ('Core', $iconPlaceholder)
        """)
    }
    
    /**
     * Insert seed data for gym exercises
     * This method is called during database creation
     * 
     * Muscle Group IDs:
     * 1 = Chest, 2 = Back, 3 = Shoulders, 4 = Arms, 5 = Legs, 6 = Core
     */
    private fun insertGymExercisesSeedData(db: SQLiteDatabase) {
        val thumbnailPlaceholder = com.example.yugved4.R.drawable.ic_activity
        
        // ===== CHEST EXERCISES (muscle_id = 1) =====
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (1, 'Push-ups', 'Classic bodyweight chest exercise. Place hands shoulder-width apart, keep your body straight, and lower yourself until your chest nearly touches the ground. Push back up to starting position. Excellent for building upper body strength.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/chest/Push-ups.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (1, 'Bench Press', 'Lie flat on a bench with feet firmly on the floor. Grip the barbell slightly wider than shoulder-width. Lower the bar to your mid-chest in a controlled motion, then press it back up to the starting position. Keep your shoulder blades retracted and core engaged throughout.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/chest/Bench Press.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (1, 'Dumbbell Flyes', 'Lie on a bench holding dumbbells above your chest with palms facing each other. Lower the weights out to the sides in a wide arc with a slight bend in your elbows, feeling a stretch across your chest. Bring them back up in a hugging motion, squeezing your pecs at the top.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/chest/Chest fly.mp4')
        """)
        
        // ===== BACK EXERCISES (muscle_id = 2) =====
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (2, 'Lat Pulldowns', 'Sit at a lat pulldown machine and grip the bar wider than shoulder-width. Pull the bar down to your upper chest while keeping your torso upright. Squeeze your shoulder blades together at the bottom, then slowly return to the starting position. Focus on pulling with your lats, not your arms.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/back/Lat Pulldowns.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (2, 'Seated Row', 'Sit at a cable row machine with feet on the platform and knees slightly bent. Pull the handle to your abdomen, keeping your back straight and squeezing your shoulder blades together. Slowly extend your arms back to the starting position while maintaining tension.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/back/Seated Row.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (2, 'Deadlifts', 'Stand with feet hip-width apart, barbell over mid-foot. Bend at hips and knees to grip the bar with hands just outside your legs. Keep your back straight, chest up, and core tight. Lift by extending hips and knees simultaneously, keeping the bar close to your body. Lower with control.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/back/DeadLift.mp4')
        """)
        
        // ===== SHOULDERS EXERCISES (muscle_id = 3) =====
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (3, 'Overhead Press', 'Stand with feet shoulder-width apart, holding dumbbells or a barbell at shoulder height. Press the weight directly overhead until arms are fully extended, keeping your core engaged. Lower back to shoulder level with control. Avoid arching your lower back.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/shoulders/Overhead Press.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (3, 'Lateral Raise', 'Stand holding dumbbells at your sides with palms facing inward. Raise the weights out to the sides with a slight bend in your elbows until arms are parallel to the floor. Focus on leading with your elbows. Lower slowly back to starting position, maintaining control.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/shoulders/Lateral Raise.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (3, 'Front Raises', 'Stand holding dumbbells in front of your thighs. Raise one or both weights forward and upward to shoulder height, keeping arms straight but not locked. Slowly lower back down and repeat. This targets the front deltoids.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/shoulders/Front Raises.mp4')
        """)
        
        // ===== ARMS EXERCISES (muscle_id = 4) =====
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (4, 'Bicep Curls', 'Stand with dumbbells at your sides, palms facing forward. Curl the weights up towards your shoulders by bending at the elbows, keeping your upper arms stationary. Squeeze your biceps at the top, then slowly lower the weights back down. Avoid swinging or using momentum.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/arms/Bicep Curls.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (4, 'Tricep Pushdowns', 'Stand facing a cable machine with a rope or bar attachment set at upper chest height. Grip the attachment and push down until your arms are fully extended, keeping your elbows close to your sides. Squeeze your triceps at the bottom, then slowly return to the starting position.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/arms/Tricep Pushdowns.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (4, 'Hammer Curls', 'Stand with dumbbells at your sides, palms facing each other (neutral grip). Curl the weights towards your shoulders while maintaining the neutral grip throughout. This variation targets both biceps and forearms. Lower with control.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/arms/Hammer Curls.mp4')
        """)
        
        // ===== LEGS EXERCISES (muscle_id = 5) =====
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (5, 'Squats', 'Stand with feet shoulder-width apart, toes slightly pointed out. Lower your hips as if sitting back into a chair, keeping your chest up and weight on your heels. Descend until thighs are parallel to the ground. Drive through your heels to return to standing.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/legs/Squats.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (5, 'Leg Press', 'Sit in a leg press machine with feet shoulder-width apart on the platform. Release the safety handles and lower the platform by bending your knees until they reach 90 degrees. Push through your heels to extend your legs back to the starting position. Keep your back flat against the seat.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/legs/Leg Press.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (5, 'Lunges', 'Stand with feet hip-width apart. Step forward with one leg and lower your hips until both knees are bent at approximately 90 degrees. The back knee should nearly touch the ground. Push back to the starting position through your front heel and alternate legs.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/legs/Lunges.mp4')
        """)
        
        // ===== CORE EXERCISES (muscle_id = 6) =====
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (6, 'Planks', 'Start in a push-up position with forearms on the ground, elbows directly under shoulders. Keep your body in a straight line from head to heels, engaging your core, glutes, and quads. Hold this position without letting your hips sag or pike up. Breathe steadily.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/core/Plank mp4.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (6, 'Crunches', 'Lie on your back with knees bent and feet flat on the floor. Place hands behind your head or across your chest. Lift your shoulder blades off the ground by contracting your abs, bringing your ribs toward your hips. Lower back down with control without fully relaxing.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/core/Crunches.mp4')
        """)
        
        db.execSQL("""
            INSERT INTO $TABLE_GYM_EXERCISES 
            ($COLUMN_EXERCISE_MUSCLE_ID, $COLUMN_EXERCISE_NAME, $COLUMN_EXERCISE_DESCRIPTION, $COLUMN_EXERCISE_THUMBNAIL, $COLUMN_EXERCISE_VIDEO_URL) 
            VALUES (6, 'Russian Twists', 'Sit on the floor with knees bent and feet lifted. Lean back slightly to engage your core. Hold your hands together or hold a weight. Rotate your torso from side to side, touching the ground beside your hip with each twist. Keep your core tight throughout.', $thumbnailPlaceholder, 'file:///android_asset/videos/gym/core/Russian Twists.mp4')
        """)
    }
    
    /**
     * Insert seed data for yoga asanas
     * This method is called during database creation
     * 
     * 10 Asanas across difficulty levels:
     * Beginner (3), Intermediate (4), Advanced (3)
     */
    private fun insertYogaAsanasSeedData(db: SQLiteDatabase) {
        val thumbnailPlaceholder = com.example.yugved4.R.drawable.ic_yoga
        
        // ===== BEGINNER ASANAS (3) =====
        
        // 1. Tadasana (Mountain Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Mountain Pose',
                'Tadasana',
                'Stand with feet together, arms at your sides. Distribute your weight evenly across both feet. Engage your thigh muscles and lift your kneecaps. Draw your shoulders back and down, lengthening through the crown of your head. This foundational pose improves posture, balance, and body awareness while strengthening the legs.',
                'Improves posture and body awareness,Strengthens thighs knees and ankles,Firms abdomen and buttocks,Relieves sciatica,Reduces flat feet',
                'Hold for 30-60 seconds',
                'Beginner',
                'Standing',
                $thumbnailPlaceholder
            )
        """)
        
        // 2. Balasana (Child's Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Child''s Pose',
                'Balasana',
                'Kneel on the floor, touch your big toes together and sit on your heels. Separate your knees about hip-width apart. Exhale and lay your torso down between your thighs. Extend your arms forward with palms facing down, or rest them alongside your body with palms up. This resting pose gently stretches the back, hips, and thighs while promoting relaxation and stress relief.',
                'Gently stretches hips thighs and ankles,Calms the brain and relieves stress and fatigue,Relieves back and neck pain when done with head supported,Massages internal organs',
                'Hold for 1-3 minutes',
                'Beginner',
                'Resting',
                $thumbnailPlaceholder
            )
        """)
        
        // 3. Sukhasana (Easy Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Easy Pose',
                'Sukhasana',
                'Sit on the floor with legs crossed. Place each foot beneath the opposite knee. Press your sitting bones down into the floor and lengthen your spine. Rest your hands on your knees with palms up or down. Relax your shoulders away from your ears. This classic meditation pose calms the mind and opens the hips.',
                'Calms the mind and reduces stress,Strengthens the back,Stretches knees and ankles,Opens the hips,Improves posture',
                'Hold for 5-10 minutes',
                'Beginner',
                'Seated',
                $thumbnailPlaceholder
            )
        """)
        
        // ===== INTERMEDIATE ASANAS (4) =====
        
        // 4. Adho Mukha Svanasana (Downward-Facing Dog)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Downward-Facing Dog',
                'Adho Mukha Svanasana',
                'Start on hands and knees. Spread your fingers wide and press firmly through your palms. Tuck your toes and lift your hips up and back, forming an inverted V-shape. Keep your arms straight and press your chest toward your thighs. Work on straightening your legs and pressing your heels toward the floor. This energizing pose is one of the most recognized yoga poses.',
                'Calms the brain and relieves stress,Energizes the body,Stretches shoulders hamstrings calves arches and hands,Strengthens arms and legs,Helps relieve headache and insomnia',
                'Hold for 1-3 minutes',
                'Intermediate',
                'Inversion',
                $thumbnailPlaceholder
            )
        """)
        
        // 5. Virabhadrasana I (Warrior I)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Warrior I',
                'Virabhadrasana I',
                'Step your right foot forward into a lunge, keeping your back leg straight and turned slightly outward. Bend your front knee to 90 degrees, ensuring it stays directly over your ankle. Square your hips to the front. Raise your arms overhead, palms facing each other. Look up toward your hands. Hold, then switch sides. This powerful pose builds strength and stamina.',
                'Strengthens shoulders arms legs and back muscles,Opens the chest lungs shoulders and hips,Improves focus balance and stability,Stretches hip flexors,Builds stamina and endurance',
                'Hold for 30-60 seconds per side',
                'Intermediate',
                'Standing',
                $thumbnailPlaceholder
            )
        """)
        
        // 6. Trikonasana (Triangle Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Triangle Pose',
                'Trikonasana',
                'Stand with feet wide apart. Turn your right foot out 90 degrees and left foot slightly in. Extend your arms parallel to the floor. Reach your right hand toward your right foot, hinging at the hip. Place your right hand on your shin, ankle, or the floor. Extend your left arm straight up. Gaze at your top hand. This pose strengthens and stretches the entire body.',
                'Stretches legs groins hips and spine,Opens the chest and shoulders,Strengthens thighs knees and ankles,Stimulates abdominal organs,Improves balance and concentration',
                'Hold for 30-60 seconds per side',
                'Intermediate',
                'Standing',
                $thumbnailPlaceholder
            )
        """)
        
        // 7. Bhujangasana (Cobra Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Cobra Pose',
                'Bhujangasana',
                'Lie face down with palms flat beside your chest. Press your hips and thighs into the floor. Inhale and straighten your arms, lifting your chest off the floor. Keep your elbows slightly bent and shoulders away from your ears. Draw your shoulder blades toward each other. Gaze slightly upward. This gentle backbend opens the chest and strengthens the spine.',
                'Strengthens the spine and back muscles,Stretches chest shoulders and abdomen,Firms the buttocks,Stimulates abdominal organs,Relieves stress and fatigue,Opens the heart and lungs',
                'Hold for 15-30 seconds',
                'Intermediate',
                'Backbend',
                $thumbnailPlaceholder
            )
        """)
        
        // ===== ADVANCED ASANAS (3) =====
        
        // 8. Sirsasana (Headstand)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Headstand',
                'Sirsasana',
                'Kneel and interlace your fingers, forming a basket with your hands. Place the crown of your head on the floor inside your hands. Straighten your legs and walk your feet toward your head. Engage your core and slowly lift both legs up until perpendicular to the floor. Hold steady, distributing weight across your forearms. This is the king of asanas, requiring strength, balance, and practice. Only attempt with proper instruction.',
                'Increases blood flow to the brain,Strengthens arms shoulders and core,Improves balance and focus,Stimulates pituitary and pineal glands,Calms the mind,Builds confidence',
                'Hold for 10 seconds to 5 minutes gradually',
                'Advanced',
                'Inversion',
                $thumbnailPlaceholder
            )
        """)
        
        // 9. Bakasana (Crow Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Crow Pose',
                'Bakasana',
                'Squat with feet together. Place your palms flat on the floor, shoulder-width apart. Bend your elbows slightly and place your knees on the backs of your upper arms. Lean forward, shifting your weight onto your hands. Lift one foot off the ground, then the other. Engage your core and gaze forward. Balance on your hands. This challenging arm balance builds upper body strength and mental focus.',
                'Strengthens arms wrists shoulders and core,Stretches the upper back,Improves balance and body awareness,Builds concentration and confidence,Tones the abdominal organs',
                'Hold for 20-60 seconds',
                'Advanced',
                'Arm Balance',
                $thumbnailPlaceholder
            )
        """)
        
        // 10. Ustrasana (Camel Pose)
        db.execSQL("""
            INSERT INTO $TABLE_YOGA_ASANAS 
            ($COLUMN_ASANA_NAME, $COLUMN_SANSKRIT_NAME, $COLUMN_ASANA_DESCRIPTION, $COLUMN_ASANA_BENEFITS, $COLUMN_ASANA_DURATION, $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_CATEGORY, $COLUMN_ASANA_THUMBNAIL) 
            VALUES (
                'Camel Pose',
                'Ustrasana',
                'Kneel with knees hip-width apart. Place your hands on your lower back, fingers pointing down. Inhale and lift your chest, arching your back. If comfortable, reach back one hand at a time to grasp your heels. Push your hips forward. Drop your head back gently if it feels comfortable for your neck. This deep backbend opens the entire front of the body and requires careful practice.',
                'Stretches entire front of body including ankles thighs and abdomen,Improves posture and spinal flexibility,Strengthens back muscles,Stimulates organs of abdomen and neck,Reduces fat on thighs,Opens the heart chakra',
                'Hold for 30-60 seconds',
                'Advanced',
                'Backbend',
                $thumbnailPlaceholder
            )
        """)
    }
    
    /**
     * Insert seed data for meal plans
     * This method is called during database creation
     * 
     * 14 Meal Plans covering:
     * - 7 calorie ranges (1000-4000 kcal)
     * - 2 diet types (Veg & Non-Veg)
     */
    private fun insertMealPlansSeedData(db: SQLiteDatabase) {
        
        // ===== 1000-1200 KCAL PLANS =====
        
        // 1. 1000-1200 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1000, 1200, 'Veg',
                'Breakfast (250 kcal): Small bowl of oats porridge with half banana, a few almonds',
                'Lunch (400 kcal): 1 small roti, dal (1 small bowl), steamed vegetables, small portion of brown rice',
                'Dinner (350 kcal): Vegetable soup, 1 roti, grilled paneer (50g), cucumber salad',
                'Snacks (150 kcal): Green tea, 1 small apple, 5-6 almonds'
            )
        """)
        
        // 2. 1000-1200 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1000, 1200, 'Non-Veg',
                'Breakfast (250 kcal): 1 boiled egg, 1 slice whole wheat toast, black coffee',
                'Lunch (400 kcal): Grilled chicken breast (100g), small portion quinoa, mixed greens salad',
                'Dinner (350 kcal): Baked fish (100g), steamed broccoli, small sweet potato',
                'Snacks (150 kcal): Greek yogurt (100g), handful of berries'
            )
        """)
        
        // ===== 1200-1500 KCAL PLANS =====
        
        // 3. 1200-1500 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1200, 1500, 'Veg',
                'Breakfast (300 kcal): Oats porridge with banana, honey, and 8-10 almonds',
                'Lunch (500 kcal): 2 small rotis, rajma/chole curry, brown rice (1 bowl), mixed vegetable salad',
                'Dinner (450 kcal): Vegetable khichdi, curd, roasted papad, cucumber raita',
                'Snacks (200 kcal): Sprouts salad, green tea, 1 orange'
            )
        """)
        
        // 4. 1200-1500 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1200, 1500, 'Non-Veg',
                'Breakfast (300 kcal): 2 boiled eggs, 1 whole wheat toast, avocado (small portion)',
                'Lunch (500 kcal): Grilled chicken (120g), quinoa bowl, roasted vegetables, olive oil dressing',
                'Dinner (450 kcal): Fish curry, small portion rice, sautéed spinach',
                'Snacks (200 kcal): Protein shake, apple, 10 almonds'
            )
        """)
        
        // ===== 1500-1800 KCAL PLANS =====
        
        // 5. 1500-1800 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1500, 1800, 'Veg',
                'Breakfast (400 kcal): Poha/upma with peanuts, 1 glass milk, 1 banana',
                'Lunch (600 kcal): 3 rotis, dal makhani, paneer sabzi, brown rice, salad, curd',
                'Dinner (500 kcal): Vegetable biryani (1 bowl), raita, papad, mixed salad',
                'Snacks (250 kcal): Roasted chana, fruit (apple/orange), green tea, handful of nuts'
            )
        """)
        
        // 6. 1500-1800 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1500, 1800, 'Non-Veg',
                'Breakfast (400 kcal): 2 eggs (scrambled), 2 slices whole grain toast, orange juice',
                'Lunch (600 kcal): Chicken tikka (150g), brown rice, dal, mixed vegetables, salad',
                'Dinner (500 kcal): Grilled fish/prawns, quinoa, steamed broccoli, tomato soup',
                'Snacks (250 kcal): Whey protein shake, banana, peanut butter (1 tbsp)'
            )
        """)
        
        // ===== 1800-2000 KCAL PLANS =====
        
        // 7. 1800-2000 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1800, 2000, 'Veg',
                'Breakfast (450 kcal): Paratha (2), curd, pickle, 1 glass milk/chai',
                'Lunch (650 kcal): 3 rotis, rajma/chole, paneer curry, rice, salad, buttermilk',
                'Dinner (550 kcal): Dal, 2 rotis, mix veg sabzi, curd, salad',
                'Snacks (300 kcal): Smoothie with fruits and nuts, roasted makhana, green tea'
            )
        """)
        
        // 8. 1800-2000 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                1800, 2000, 'Non-Veg',
                'Breakfast (450 kcal): 3 eggs omelette, 2 toast slices, avocado, orange juice',
                'Lunch (650 kcal): Chicken curry (150g), 2 rotis, rice, dal, salad, curd',
                'Dinner (550 kcal): Grilled fish, sweet potato, mixed vegetables, soup',
                'Snacks (300 kcal): Protein bar, banana, handful of mixed nuts, green tea'
            )
        """)
        
        // ===== 2000-2500 KCAL PLANS =====
        
        // 9. 2000-2500 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                2000, 2500, 'Veg',
                'Breakfast (500 kcal): 2 parathas with curd, pickle, 1 glass milk, banana',
                'Lunch (800 kcal): 4 rotis, dal makhani, paneer butter masala, rice, salad, raita, papad',
                'Dinner (650 kcal): Vegetable pulao, dal, raita, paneer tikka, salad',
                'Snacks (400 kcal): Smoothie bowl with fruits and nuts, sandwich, tea/coffee'
            )
        """)
        
        // 10. 2000-2500 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                2000, 2500, 'Non-Veg',
                'Breakfast (500 kcal): 3 eggs, 3 toast slices, avocado, bacon (2 strips), juice',
                'Lunch (800 kcal): Chicken biryani (large bowl), raita, boiled eggs (2), salad',
                'Dinner (650 kcal): Grilled chicken (200g), brown rice, mixed vegetables, dal',
                'Snacks (400 kcal): Whey shake, peanut butter sandwich, fruits, almonds'
            )
        """)
        
        // ===== 2500-3000 KCAL PLANS =====
        
        // 11. 2500-3000 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                2500, 3000, 'Veg',
                'Breakfast (600 kcal): 3 parathas, curd, paneer bhurji, milk, fruits',
                'Lunch (950 kcal): 5 rotis, chole/rajma, paneer curry, rice (2 bowls), salad, raita, dessert',
                'Dinner (750 kcal): Dal makhani, 3 rotis, veg pulao, paneer tikka, salad, curd',
                'Snacks (500 kcal): Dry fruits smoothie, sandwich, pakoras, tea, fruits'
            )
        """)
        
        // 12. 2500-3000 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                2500, 3000, 'Non-Veg',
                'Breakfast (600 kcal): 4 eggs, 3 toast, bacon, avocado, protein shake',
                'Lunch (950 kcal): Chicken curry (250g), 3 rotis, rice (2 bowls), dal, eggs, salad',
                'Dinner (750 kcal): Grilled fish/chicken (250g), pasta, vegetables, soup',
                'Snacks (500 kcal): Mass gainer shake, chicken sandwich, fruits, nuts, protein bar'
            )
        """)
        
        // ===== 3000-4000 KCAL PLANS =====
        
        // 13. 3000-4000 kcal - Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                3000, 4000, 'Veg',
                'Breakfast (750 kcal): 4 parathas, paneer bhurji, curd, milk, dry fruits, banana',
                'Lunch (1200 kcal): 6 rotis, rajma/chole, paneer butter masala, rice (3 bowls), salad, raita, sweet dish',
                'Dinner (900 kcal): Veg biryani (large), dal makhani, paneer tikka, raita, salad, dessert',
                'Snacks (700 kcal): Mass gainer shake, sandwich, pakoras, dry fruits, smoothie, tea'
            )
        """)
        
        // 14. 3000-4000 kcal - Non-Vegetarian
        db.execSQL("""
            INSERT INTO $TABLE_MEAL_PLANS 
            ($COLUMN_MIN_CALORIES, $COLUMN_MAX_CALORIES, $COLUMN_DIET_TYPE, $COLUMN_BREAKFAST, $COLUMN_LUNCH, $COLUMN_DINNER, $COLUMN_SNACKS) 
            VALUES (
                3000, 4000, 'Non-Veg',
                'Breakfast (750 kcal): 5 eggs, 4 toast, bacon, cheese, avocado, protein shake, juice',
                'Lunch (1200 kcal): Chicken biryani (xl), 3 boiled eggs, chicken tikka, raita, salad',
                'Dinner (900 kcal): Grilled chicken/fish (300g), rice (2 bowls), pasta, vegetables, soup',
                'Snacks (700 kcal): Mass gainer shake (2), chicken sandwich, fruits, nuts, protein bars (2)'
            )
        """)
    }
    
    /**
     * Get all muscle groups from database
     * @return List of MuscleGroup objects
     */
    fun getAllMuscleGroups(): List<com.example.yugved4.models.MuscleGroup> {
        val muscleGroupList = mutableListOf<com.example.yugved4.models.MuscleGroup>()
        val db = readableDatabase
        
        db.rawQuery("SELECT * FROM $TABLE_MUSCLE_GROUPS", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MUSCLE_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MUSCLE_NAME))
                    val imageResource = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MUSCLE_IMAGE))
                    
                    muscleGroupList.add(
                        com.example.yugved4.models.MuscleGroup(
                            id = id,
                            name = name,
                            imageResource = imageResource
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return muscleGroupList
    }
    
    /**
     * Get exercises by muscle ID using RAW SQL query
     * @param muscleId The ID of the muscle group
     * @return List of GymExercise objects
     */
    fun getExercisesByMuscleId(muscleId: Int): List<GymExercise> {
        val exerciseList = mutableListOf<GymExercise>()
        val db = readableDatabase
        
        // Raw SQL query with WHERE clause
        val query = "SELECT * FROM $TABLE_GYM_EXERCISES WHERE $COLUMN_EXERCISE_MUSCLE_ID = ?"
        
        db.rawQuery(query, arrayOf(muscleId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_ID))
                    val exerciseMuscleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_MUSCLE_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_DESCRIPTION))
                    val thumbnail = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_THUMBNAIL))
                    val videoUrl = if (cursor.getColumnIndex(COLUMN_EXERCISE_VIDEO_URL) != -1) {
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_VIDEO_URL))
                    } else null
                    
                    exerciseList.add(
                        GymExercise(
                            id = id,
                            muscleId = exerciseMuscleId,
                            name = name,
                            description = description,
                            thumbnailResource = thumbnail,
                            videoUrl = videoUrl
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return exerciseList
    }
    
    /**
     * Get exercise detail by exercise ID
     * @param exerciseId The ID of the exercise
     * @return GymExercise object or null if not found
     */
    fun getExerciseDetail(exerciseId: Int): GymExercise? {
        val db = readableDatabase
        var exercise: GymExercise? = null
        
        val query = "SELECT * FROM $TABLE_GYM_EXERCISES WHERE $COLUMN_EXERCISE_ID = ?"
        
        db.rawQuery(query, arrayOf(exerciseId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_ID))
                val muscleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_MUSCLE_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_DESCRIPTION))
                val thumbnail = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_THUMBNAIL))
                val videoUrl = if (cursor.getColumnIndex(COLUMN_EXERCISE_VIDEO_URL) != -1) {
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_VIDEO_URL))
                } else null
                
                exercise = GymExercise(
                    id = id,
                    muscleId = muscleId,
                    name = name,
                    description = description,
                    thumbnailResource = thumbnail,
                    videoUrl = videoUrl
                )
            }
        }
        db.close()
        return exercise
    }
    
    // =====================================================
    // YOGA ASANAS QUERY METHODS
    // =====================================================
    
    /**
     * Data class representing a yoga asana from database
     */
    data class YogaAsana(
        val id: Int,
        val name: String,
        val sanskritName: String,
        val description: String,
        val benefits: List<String>,  // Parsed from comma-separated string
        val duration: String,
        val difficultyLevel: String,
        val category: String,
        val thumbnailResource: Int,
        val videoUrl: String? = null
    )
    
    /**
     * Get all yoga asanas from database
     * @return List of YogaAsana objects
     */
    fun getAllYogaAsanas(): List<YogaAsana> {
        val asanaList = mutableListOf<YogaAsana>()
        val db = readableDatabase
        
        db.rawQuery("SELECT * FROM $TABLE_YOGA_ASANAS ORDER BY $COLUMN_ASANA_DIFFICULTY, $COLUMN_ASANA_NAME", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ASANA_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_NAME))
                    val sanskritName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SANSKRIT_NAME))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DESCRIPTION))
                    val benefitsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_BENEFITS))
                    val benefits = benefitsString.split(",").map { it.trim() }  // Parse comma-separated benefits
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DURATION))
                    val difficultyLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DIFFICULTY))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_CATEGORY))
                    val thumbnailResource = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ASANA_THUMBNAIL))
                    val videoUrl = if (cursor.getColumnIndex(COLUMN_ASANA_VIDEO_URL) != -1 && !cursor.isNull(cursor.getColumnIndex(COLUMN_ASANA_VIDEO_URL))) {
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_VIDEO_URL))
                    } else null
                    
                    asanaList.add(
                        YogaAsana(id, name, sanskritName, description, benefits, duration, difficultyLevel, category, thumbnailResource, videoUrl)
                    )
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return asanaList
    }
    
    /**
     * Get yoga asanas filtered by category
     * @param category Category to filter by (e.g., "Beginner", "Intermediate", "Advanced")
     * @return List of YogaAsana objects matching the category
     */
    fun getAsanasByCategory(category: String): List<YogaAsana> {
        val asanaList = mutableListOf<YogaAsana>()
        val db = readableDatabase
        
        // Query with WHERE clause filtering by difficulty level
        val query = "SELECT * FROM $TABLE_YOGA_ASANAS WHERE $COLUMN_ASANA_DIFFICULTY = ? ORDER BY $COLUMN_ASANA_NAME"
        
        db.rawQuery(query, arrayOf(category)).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ASANA_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_NAME))
                    val sanskritName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SANSKRIT_NAME))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DESCRIPTION))
                    val benefitsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_BENEFITS))
                    val benefits = benefitsString.split(",").map { it.trim() }
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DURATION))
                    val difficultyLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DIFFICULTY))
                    val categoryValue = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_CATEGORY))
                    val thumbnailResource = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ASANA_THUMBNAIL))
                    val videoUrl = if (cursor.getColumnIndex(COLUMN_ASANA_VIDEO_URL) != -1 && !cursor.isNull(cursor.getColumnIndex(COLUMN_ASANA_VIDEO_URL))) {
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_VIDEO_URL))
                    } else null
                    
                    asanaList.add(
                        YogaAsana(id, name, sanskritName, description, benefits, duration, difficultyLevel, categoryValue, thumbnailResource, videoUrl)
                    )
                } while (cursor.moveToNext())
            }
        }
        
        db.close()
        return asanaList
    }
    
    /**
     * Get specific yoga asana by ID
     * @param asanaId ID of the asana
     * @return YogaAsana object or null if not found
     */
    fun getAsanaById(asanaId: Int): YogaAsana? {
        val db = readableDatabase
        var asana: YogaAsana? = null
        
        // Query with WHERE clause filtering by asana_id
        val query = "SELECT * FROM $TABLE_YOGA_ASANAS WHERE $COLUMN_ASANA_ID = ?"
        
        db.rawQuery(query, arrayOf(asanaId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ASANA_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_NAME))
                val sanskritName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SANSKRIT_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DESCRIPTION))
                val benefitsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_BENEFITS))
                val benefits = benefitsString.split(",").map { it.trim() }
                val duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DURATION))
                val difficultyLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_DIFFICULTY))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_CATEGORY))
                val thumbnailResource = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ASANA_THUMBNAIL))
                val videoUrl = if (cursor.getColumnIndex(COLUMN_ASANA_VIDEO_URL) != -1 && !cursor.isNull(cursor.getColumnIndex(COLUMN_ASANA_VIDEO_URL))) {
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASANA_VIDEO_URL))
                } else null
                
                asana = YogaAsana(id, name, sanskritName, description, benefits, duration, difficultyLevel, category, thumbnailResource, videoUrl)
            }
        }
        
        db.close()
        return asana
    }

    // =====================================================
    // MEAL PLANS QUERY METHODS
    // =====================================================
    
    /**
     * Data class representing a meal plan from database
     */
    data class MealPlan(
        val id: Int,
        val minCalories: Int,
        val maxCalories: Int,
        val dietType: String,
        val breakfast: String,
        val lunch: String,
        val dinner: String,
        val snacks: String
    )
    
    /**
     * Get meal plan based on calorie target and diet type
     * Uses BETWEEN operator for range matching
     * @param targetCalories The user's calculated calorie requirement
     * @param dietType Diet preference ('Veg' or 'Non-Veg')
     * @return MealPlan object or null if no match found
     */
    fun getMealPlan(targetCalories: Int, dietType: String): MealPlan? {
        val db = readableDatabase
        var plan: MealPlan? = null
        
        // Primary query: Find plan where targetCalories falls within the range
        val query = """
            SELECT * FROM $TABLE_MEAL_PLANS 
            WHERE ? BETWEEN $COLUMN_MIN_CALORIES AND $COLUMN_MAX_CALORIES 
            AND $COLUMN_DIET_TYPE = ?
            ORDER BY $COLUMN_PLAN_ID LIMIT 1
        """
        
        db.rawQuery(query, arrayOf(targetCalories.toString(), dietType)).use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAN_ID))
                val minCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIN_CALORIES))
                val maxCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_CALORIES))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIET_TYPE))
                val breakfast = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREAKFAST))
                val lunch = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LUNCH))
                val dinner = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DINNER))
                val snacks = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SNACKS))
                
                plan = MealPlan(id, minCal, maxCal, type, breakfast, lunch, dinner, snacks)
            }
        }
        
        // Fallback: If no exact match, find closest plan
        if (plan == null) {
            val fallbackQuery = if (targetCalories < 1000) {
                // If below minimum, return lowest calorie plan
                """
                    SELECT * FROM $TABLE_MEAL_PLANS 
                    WHERE $COLUMN_DIET_TYPE = ?
                    ORDER BY $COLUMN_MIN_CALORIES ASC LIMIT 1
                """
            } else {
                // If above maximum, return highest calorie plan
                """
                    SELECT * FROM $TABLE_MEAL_PLANS 
                    WHERE $COLUMN_DIET_TYPE = ?
                    ORDER BY $COLUMN_MAX_CALORIES DESC LIMIT 1
                """
            }
            
            db.rawQuery(fallbackQuery, arrayOf(dietType)).use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAN_ID))
                    val minCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIN_CALORIES))
                    val maxCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_CALORIES))
                    val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIET_TYPE))
                    val breakfast = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREAKFAST))
                    val lunch = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LUNCH))
                    val dinner = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DINNER))
                    val snacks = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SNACKS))
                    
                    plan = MealPlan(id, minCal, maxCal, type, breakfast, lunch, dinner, snacks)
                }
            }
        }
        
        db.close()
        return plan
    }

    // =====================================================
    // DOCTOR PROFILE METHODS
    // =====================================================
    
    /**
     * Data class for Gym Exercise
     */
    data class GymExercise(
        val id: Int,
        val muscleId: Int,
        val name: String,
        val description: String,
        val thumbnailResource: Int,
        val videoUrl: String? = null
    )
    
    // =====================================================
    // STEP COUNTER SETTINGS METHODS
    // =====================================================
    
    /**
     * Set step counter enabled/disabled state
     * @param isEnabled True to enable step tracking, false to disable
     */
    fun setStepCounterEnabled(isEnabled: Boolean) {
        val db = writableDatabase
        val value = if (isEnabled) 1 else 0
        
        // Use INSERT OR REPLACE to update existing or insert new
        db.execSQL(
            "INSERT OR REPLACE INTO $TABLE_APP_SETTINGS ($COLUMN_SETTING_KEY, $COLUMN_SETTING_VALUE) VALUES (?, ?)",
            arrayOf("step_counter_enabled", value)
        )
        
        db.close()
    }
    
    /**
     * Get step counter enabled/disabled state
     * @return True if step tracking is enabled, false otherwise
     */
    fun getStepCounterEnabled(): Boolean {
        val db = readableDatabase
        var isEnabled = false
        
        db.rawQuery(
            "SELECT $COLUMN_SETTING_VALUE FROM $TABLE_APP_SETTINGS WHERE $COLUMN_SETTING_KEY = ?",
            arrayOf("step_counter_enabled")
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                isEnabled = cursor.getInt(0) == 1
            }
        }
        
        db.close()
        return isEnabled
    }
    
    // =====================================================
    // DAILY STEP TRACKING METHODS
    // =====================================================
    
    /**
     * Save step count for a specific date
     * @param date Date in format "YYYY-MM-DD"
     * @param stepCount Number of steps
     */
    fun saveStepCount(date: String, stepCount: Int) {
        val db = writableDatabase
        
        // Use INSERT OR REPLACE to update existing or insert new
        db.execSQL(
            "INSERT OR REPLACE INTO $TABLE_DAILY_STEPS ($COLUMN_STEP_DATE, $COLUMN_STEP_COUNT) VALUES (?, ?)",
            arrayOf(date, stepCount)
        )
        
        db.close()
    }
    
    /**
     * Get step count for a specific date
     * @param date Date in format "YYYY-MM-DD"
     * @return Step count or 0 if no data found
     */
    fun getStepCount(date: String): Int {
        val db = readableDatabase
        var stepCount = 0
        
        db.rawQuery(
            "SELECT $COLUMN_STEP_COUNT FROM $TABLE_DAILY_STEPS WHERE $COLUMN_STEP_DATE = ?",
            arrayOf(date)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                stepCount = cursor.getInt(0)
            }
        }
        
        db.close()
        return stepCount
    }
    
    /**
     * Get step count for today
     * @return Step count or 0 if no data found
     */
    fun getTodayStepCount(): Int {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        return getStepCount(today)
    }
    
    // =============== FIREBASE & AUTHENTICATION METHODS ===============
    
    /**
     * Get Firebase UID from user profile
     * @return Firebase UID string or null if not found
     */
    fun getFirebaseUid(): String? {
        val db = readableDatabase
        var uid: String? = null
        
        db.rawQuery("SELECT $COLUMN_FIREBASE_UID FROM $TABLE_USER_PROFILE LIMIT 1", null).use { cursor ->
            if (cursor.moveToFirst()) {
                uid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIREBASE_UID))
            }
        }
        
        db.close()
        return uid
    }
    
    /**
     * Update Firebase UID in user profile
     * @return number of rows affected
     */
    fun updateFirebaseUid(uid: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIREBASE_UID, uid)
        }
        
        val rowsAffected = db.update(TABLE_USER_PROFILE, values, null, null)
        db.close()
        return rowsAffected
    }
    
    // =============== STEP GOAL METHODS ===============
    
    /**
     * Save step goal to app settings
     * @param goal Daily step goal
     * @return row ID or -1 if error
     */
    fun saveStepGoal(goal: Int): Long {
        val db = writableDatabase
        
        // Delete existing step goal
        db.delete(TABLE_APP_SETTINGS, "$COLUMN_SETTING_KEY = ?", arrayOf("step_goal"))
        
        val values = ContentValues().apply {
            put(COLUMN_SETTING_KEY, "step_goal")
            put(COLUMN_SETTING_VALUE, goal)
        }
        
        val id = db.insert(TABLE_APP_SETTINGS, null, values)
        db.close()
        return id
    }
    
    /**
     * Get step goal from app settings
     * @return Step goal or default 10000 if not found
     */
    fun getStepGoal(): Int {
        val db = readableDatabase
        var goal = 10000 // Default goal
        
        db.rawQuery(
            "SELECT $COLUMN_SETTING_VALUE FROM $TABLE_APP_SETTINGS WHERE $COLUMN_SETTING_KEY = ?",
            arrayOf("step_goal")
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                goal = cursor.getInt(0)
            }
        }
        
        db.close()
        return goal
    }
}
