package com.example.yugved4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.yugved4.database.DatabaseHelper
import com.example.yugved4.databinding.ActivityLoginBinding
import com.example.yugved4.utils.AuthHelper
import com.example.yugved4.utils.FirestoreHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var dbHelper: DatabaseHelper

    companion object {
        private const val TAG = "LoginActivity"
    }

    // Activity result launcher for Google Sign-In
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            hideLoading()
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        dbHelper = DatabaseHelper(this)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up click listener for Google Sign-In button
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        showLoading()
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account == null) {
            hideLoading()
            return
        }

        Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    
                    if (user != null) {
                        // Save user to Firestore
                        saveUserToFirestore(user.uid, user.email ?: "", user.displayName ?: "User")
                        
                        // Check if user has completed profile setup
                        checkUserProfileAndNavigate()
                    }
                } else {
                    // Sign in failed
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    hideLoading()
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", 
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(uid: String, email: String, name: String) {
        lifecycleScope.launch {
            try {
                val userData = mapOf(
                    "email" to email,
                    "name" to name,
                    "createdAt" to System.currentTimeMillis()
                )
                
                // Check if user already exists in Firestore
                val existingProfile = FirestoreHelper.getUserProfile(uid)
                if (existingProfile == null) {
                    // New user - save basic info
                    FirestoreHelper.saveUserProfile(uid, userData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving user to Firestore", e)
                // Continue even if Firestore save fails
            }
        }
    }

    private fun checkUserProfileAndNavigate() {
        val currentUser = AuthHelper.getCurrentUser()
        if (currentUser == null) {
            hideLoading()
            return
        }

        // Check if user has completed profile in local database
        val hasProfile = dbHelper.hasUserProfile()

        hideLoading()

        if (hasProfile) {
            // User has completed profile - go to main app
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User needs to complete profile - go to onboarding
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
        finish()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGoogleSignIn.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnGoogleSignIn.isEnabled = true
    }
}
