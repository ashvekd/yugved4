package com.example.yugved4.fragments

import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yugved4.R
import com.example.yugved4.adapters.HelplineCardAdapter
import com.example.yugved4.database.DatabaseHelper
import com.google.android.material.button.MaterialButton

/**
 * Mental Health Fragment - Complete mental wellness toolkit
 * Features: Mood tracking, breathing exercises, relaxation audio, daily quotes, helplines
 */
class MentalHealthFragment : Fragment() {

    private lateinit var rvHelplines: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    
    // UI Components
    private lateinit var tvDailyQuote: TextView
    private lateinit var breathingCircle: View
    private lateinit var btnToggleAudio: MaterialButton
    private lateinit var btnMood1: Button
    private lateinit var btnMood2: Button
    private lateinit var btnMood3: Button
    private lateinit var btnMood4: Button
    private lateinit var btnMood5: Button
    
    // Breathing animation
    private var breathingAnimator: ValueAnimator? = null
    
    // Audio player
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mental_health, container, false)

        // Initialize database helper
        dbHelper = DatabaseHelper(requireContext())

        // Initialize views
        tvDailyQuote = view.findViewById(R.id.tvDailyQuote)
        breathingCircle = view.findViewById(R.id.breathingCircle)
        btnToggleAudio = view.findViewById(R.id.btnToggleAudio)
        btnMood1 = view.findViewById(R.id.btnMood1)
        btnMood2 = view.findViewById(R.id.btnMood2)
        btnMood3 = view.findViewById(R.id.btnMood3)
        btnMood4 = view.findViewById(R.id.btnMood4)
        btnMood5 = view.findViewById(R.id.btnMood5)
        
        // Initialize RecyclerView
        rvHelplines = view.findViewById(R.id.rvHelplines)
        setupRecyclerView()
        
        // Load random quote
        loadDailyQuote()
        
        // Setup mood buttons
        setupMoodTracking()
        
        // Setup breathing animation
        setupBreathingAnimation()
        
        // Setup audio player (only if audio file exists)
        setupAudioPlayer()

        return view
    }
    
    /**
     * Load a random quote from database and display it
     */
    private fun loadDailyQuote() {
        val quote = dbHelper.getRandomQuote()
        tvDailyQuote.text = "\"$quote\""
    }
    
    /**
     * Setup mood tracking buttons
     */
    private fun setupMoodTracking() {
        btnMood1.setOnClickListener { saveMood(1, "😫 Terrible") }
        btnMood2.setOnClickListener { saveMood(2, "😟 Not great") }
        btnMood3.setOnClickListener { saveMood(3, "😐 Okay") }
        btnMood4.setOnClickListener { saveMood(4, "🙂 Good") }
        btnMood5.setOnClickListener { saveMood(5, "😄 Excellent") }
    }
    
    /**
     * Save mood to database and show feedback
     */
    private fun saveMood(score: Int, description: String) {
        val success = dbHelper.saveDailyMood(score)
        
        if (success) {
            Toast.makeText(requireContext(), "Mood saved: $description", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to save mood", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Setup breathing animation with smooth inhale/exhale cycle
     */
    private fun setupBreathingAnimation() {
        breathingAnimator = ValueAnimator.ofFloat(1.0f, 1.5f, 1.0f).apply {
            duration = 8000 // 4 seconds inhale + 4 seconds exhale
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                breathingCircle.scaleX = scale
                breathingCircle.scaleY = scale
            }
            
            start()
        }
    }
    
    /**
     * Setup audio player for relaxation sounds
     * Note: Requires rain_sounds.mp3 in res/raw directory
     */
    private fun setupAudioPlayer() {
        btnToggleAudio.setOnClickListener {
            toggleAudio()
        }
    }
    
    /**
     * Toggle audio playback
     */
    private fun toggleAudio() {
        try {
            if (isPlaying) {
                // Pause audio
                mediaPlayer?.pause()
                isPlaying = false
                btnToggleAudio.text = "▶️ Play Sounds"
                btnToggleAudio.setIconResource(android.R.drawable.ic_media_play)
            } else {
                // Initialize if needed
                if (mediaPlayer == null) {
                    // Check if audio file exists by trying to create MediaPlayer
                    try {
                        // TODO: Add rain_sounds.mp3 to res/raw/ directory to enable this feature
                        // mediaPlayer = MediaPlayer.create(requireContext(), R.raw.rain_sounds)
                        // mediaPlayer?.isLooping = true
                        
                        // Show message that audio file is not available
                        Toast.makeText(
                            requireContext(),
                            "Audio file not found. Please add rain_sounds.mp3 to res/raw/",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Audio file not found. Please add rain_sounds.mp3 to res/raw/",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }
                
                // Play audio
                mediaPlayer?.start()
                isPlaying = true
                btnToggleAudio.text = "⏸️ Pause Sounds"
                btnToggleAudio.setIconResource(android.R.drawable.ic_media_pause)
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error playing audio: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupRecyclerView() {
        // Get all helplines from database
        val helplines = dbHelper.getAllHelplines()

        // Create adapter
        val adapter = HelplineCardAdapter(helplines)

        // Setup RecyclerView
        rvHelplines.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            setHasFixedSize(true)
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Pause audio when fragment is paused
        if (isPlaying) {
            mediaPlayer?.pause()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Resume audio if it was playing before
        if (isPlaying) {
            mediaPlayer?.start()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        
        // Clean up breathing animation
        breathingAnimator?.cancel()
        breathingAnimator = null
        
        // Clean up media player
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isPlaying = false
    }
}


