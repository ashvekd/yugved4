package com.example.yugved4

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.yugved4.database.DatabaseHelper
import com.example.yugved4.databinding.ActivityOnboardingBinding
import com.example.yugved4.databinding.StepOnboardingAgeBinding
import com.example.yugved4.databinding.StepOnboardingBodyBinding
import com.example.yugved4.databinding.StepOnboardingGoalBinding
import com.example.yugved4.databinding.StepOnboardingNameBinding
import com.example.yugved4.utils.AuthHelper
import com.example.yugved4.utils.FirestoreHelper
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var dbHelper: DatabaseHelper
    
    private var currentStep = 0
    private val totalSteps = 4
    
    // Data storage
    private var userName: String = ""
    private var userAge: Int = 0
    private var userHeight: Double = 0.0
    private var userWeight: Double = 0.0
    private var stepGoal: Int = 8000 // Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dbHelper = DatabaseHelper(this)

        // Set up navigation buttons
        binding.btnPrevious.setOnClickListener {
            if (currentStep > 0) {
                currentStep--
                updateUI()
            }
        }

        binding.btnNext.setOnClickListener {
            if (validateCurrentStep()) {
                saveCurrentStepData()
                
                if (currentStep < totalSteps - 1) {
                    currentStep++
                    updateUI()
                } else {
                    // Last step - finish onboarding
                    finishOnboarding()
                }
            }
        }

        // Show first step
        updateUI()
    }

    private fun updateUI() {
        // Update progress indicator
        val progress = ((currentStep + 1).toFloat() / totalSteps.toFloat()) * 100
        binding.progressIndicator.setProgressCompat(progress.toInt(), true)

        // Update button visibility
        binding.btnPrevious.visibility = if (currentStep == 0) View.GONE else View.VISIBLE
        binding.btnNext.text = if (currentStep == totalSteps - 1) "Finish" else "Next"

        // Show appropriate step
        when (currentStep) {
            0 -> showNameStep()
            1 -> showAgeStep()
            2 -> showBodyStep()
            3 -> showGoalStep()
        }
    }

    private fun showNameStep() {
        val stepBinding = StepOnboardingNameBinding.inflate(LayoutInflater.from(this))
        stepBinding.etName.setText(userName)
        binding.stepContainer.removeAllViews()
        binding.stepContainer.addView(stepBinding.root)
    }

    private fun showAgeStep() {
        val stepBinding = StepOnboardingAgeBinding.inflate(LayoutInflater.from(this))
        if (userAge > 0) stepBinding.etAge.setText(userAge.toString())
        binding.stepContainer.removeAllViews()
        binding.stepContainer.addView(stepBinding.root)
    }

    private fun showBodyStep() {
        val stepBinding = StepOnboardingBodyBinding.inflate(LayoutInflater.from(this))
        if (userHeight > 0) stepBinding.etHeight.setText(userHeight.toString())
        if (userWeight > 0) stepBinding.etWeight.setText(userWeight.toString())
        binding.stepContainer.removeAllViews()
        binding.stepContainer.addView(stepBinding.root)
    }

    private fun showGoalStep() {
        val stepBinding = StepOnboardingGoalBinding.inflate(LayoutInflater.from(this))
        
        // Set up chip selection
        stepBinding.chipGroupGoals.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.contains(stepBinding.chipCustom.id)) {
                stepBinding.tilCustomGoal.visibility = View.VISIBLE
            } else {
                stepBinding.tilCustomGoal.visibility = View.GONE
            }
        }
        
        // Pre-select the current goal
        when (stepGoal) {
            5000 -> stepBinding.chip5000.isChecked = true
            8000 -> stepBinding.chip8000.isChecked = true
            10000 -> stepBinding.chip10000.isChecked = true
            12000 -> stepBinding.chip12000.isChecked = true
            else -> {
                stepBinding.chipCustom.isChecked = true
                stepBinding.tilCustomGoal.visibility = View.VISIBLE
                stepBinding.etCustomGoal.setText(stepGoal.toString())
            }
        }
        
        binding.stepContainer.removeAllViews()
        binding.stepContainer.addView(stepBinding.root)
    }

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            0 -> {
                val view = binding.stepContainer.getChildAt(0)
                val nameBinding = StepOnboardingNameBinding.bind(view)
                val name = nameBinding.etName.text.toString().trim()
                
                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }
            1 -> {
                val view = binding.stepContainer.getChildAt(0)
                val ageBinding = StepOnboardingAgeBinding.bind(view)
                val ageText = ageBinding.etAge.text.toString().trim()
                
                if (ageText.isEmpty()) {
                    Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    val age = ageText.toIntOrNull()
                    if (age == null || age < 18 || age > 100) {
                        Toast.makeText(this, "Please enter a valid age (18-100)", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        true
                    }
                }
            }
            2 -> {
                val view = binding.stepContainer.getChildAt(0)
                val bodyBinding = StepOnboardingBodyBinding.bind(view)
                val heightText = bodyBinding.etHeight.text.toString().trim()
                val weightText = bodyBinding.etWeight.text.toString().trim()
                
                if (heightText.isEmpty() || weightText.isEmpty()) {
                    Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    val height = heightText.toDoubleOrNull()
                    val weight = weightText.toDoubleOrNull()
                    
                    if (height == null || height < 100 || height > 250) {
                        Toast.makeText(this, "Please enter a valid height (100-250 cm)", Toast.LENGTH_SHORT).show()
                        false
                    } else if (weight == null || weight < 30 || weight > 200) {
                        Toast.makeText(this, "Please enter a valid weight (30-200 kg)", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        true
                    }
                }
            }
            3 -> {
                val view = binding.stepContainer.getChildAt(0)
                val goalBinding = StepOnboardingGoalBinding.bind(view)
                val checkedChipId = goalBinding.chipGroupGoals.checkedChipId
                
                if (checkedChipId == -1) {
                    Toast.makeText(this, "Please select a step goal", Toast.LENGTH_SHORT).show()
                    false
                } else if (checkedChipId == goalBinding.chipCustom.id) {
                    val customGoal = goalBinding.etCustomGoal.text.toString().trim()
                    if (customGoal.isEmpty()) {
                        Toast.makeText(this, "Please enter a custom goal", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        val goal = customGoal.toIntOrNull()
                        if (goal == null || goal < 1000 || goal > 50000) {
                            Toast.makeText(this, "Please enter a valid goal (1000-50000 steps)", Toast.LENGTH_SHORT).show()
                            false
                        } else {
                            true
                        }
                    }
                } else {
                    true
                }
            }
            else -> true
        }
    }

    private fun saveCurrentStepData() {
        when (currentStep) {
            0 -> {
                val view = binding.stepContainer.getChildAt(0)
                val nameBinding = StepOnboardingNameBinding.bind(view)
                userName = nameBinding.etName.text.toString().trim()
            }
            1 -> {
                val view = binding.stepContainer.getChildAt(0)
                val ageBinding = StepOnboardingAgeBinding.bind(view)
                userAge = ageBinding.etAge.text.toString().toInt()
            }
            2 -> {
                val view = binding.stepContainer.getChildAt(0)
                val bodyBinding = StepOnboardingBodyBinding.bind(view)
                userHeight = bodyBinding.etHeight.text.toString().toDouble()
                userWeight = bodyBinding.etWeight.text.toString().toDouble()
            }
            3 -> {
                val view = binding.stepContainer.getChildAt(0)
                val goalBinding = StepOnboardingGoalBinding.bind(view)
                val checkedChipId = goalBinding.chipGroupGoals.checkedChipId
                
                stepGoal = when (checkedChipId) {
                    goalBinding.chip5000.id -> 5000
                    goalBinding.chip8000.id -> 8000
                    goalBinding.chip10000.id -> 10000
                    goalBinding.chip12000.id -> 12000
                    goalBinding.chipCustom.id -> goalBinding.etCustomGoal.text.toString().toInt()
                    else -> 8000
                }
            }
        }
    }

    private fun finishOnboarding() {
        val currentUser = AuthHelper.getCurrentUser()
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate target calories (simple BMR calculation)
        // Using Mifflin-St Jeor formula: BMR = 10*weight + 6.25*height - 5*age + 5 (for male, -161 for female)
        // For simplicity, we'll use the male formula as default
        val bmr = (10 * userWeight) + (6.25 * userHeight) - (5 * userAge) + 5
        val targetCalories = (bmr * 1.375).toInt() // Light activity multiplier

        // Save to local database (this must succeed)
        try {
            dbHelper.saveUserProfile(
                targetCalories = targetCalories,
                currentWeight = userWeight,
                age = userAge,
                height = userHeight,
                name = userName,
                firebaseUid = currentUser.uid
            )
            
            // Save step goal
            dbHelper.saveStepGoal(stepGoal)
            
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        // Save to Firestore asynchronously (don't block navigation)
        lifecycleScope.launch {
            try {
                val userData = mapOf(
                    "name" to userName,
                    "age" to userAge,
                    "height" to userHeight,
                    "weight" to userWeight,
                    "stepGoal" to stepGoal,
                    "targetCalories" to targetCalories,
                    "email" to (currentUser.email ?: ""),
                    "completedOnboarding" to true,
                    "createdAt" to System.currentTimeMillis()
                )
                
                FirestoreHelper.saveUserProfile(currentUser.uid, userData)
            } catch (e: Exception) {
                e.printStackTrace()
                // Firestore sync failed, but we already saved locally so it's okay
            }
        }
        
        // Navigate to main app immediately (don't wait for Firestore)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
