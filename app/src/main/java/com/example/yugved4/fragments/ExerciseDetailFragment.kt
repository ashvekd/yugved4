package com.example.yugved4.fragments

import java.io.File

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yugved4.database.DatabaseHelper
import com.example.yugved4.databinding.FragmentExerciseDetailBinding

/**
 * Exercise Detail Fragment showing video placeholder and exercise instructions
 * Uses SQL database to fetch exercise details by exerciseId
 */
class ExerciseDetailFragment : Fragment() {

    private var _binding: FragmentExerciseDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // Get exercise ID from arguments
        val exerciseId = arguments?.getInt("exerciseId") ?: 1

        loadExerciseDetail(exerciseId)
    }

    /**
     * Load exercise detail from database using RAW SQL query
     * SELECT * FROM gym_exercises WHERE exercise_id = ?
     */
    private fun loadExerciseDetail(exerciseId: Int) {
        val exercise = dbHelper.getExerciseDetail(exerciseId)

        exercise?.let {
            // Display exercise title
            binding.tvExerciseTitle.text = it.name

            // Display exercise description
            binding.tvExerciseDescription.text = it.description

            // Setup Video Player
            setupVideoPlayer(it.videoUrl)
        }
    }

    private fun setupVideoPlayer(videoUrl: String?) {
        if (videoUrl.isNullOrEmpty()) {
            binding.tvVideoError.text = "No video available"
            binding.tvVideoError.visibility = View.VISIBLE
            binding.videoLoadingProgress.visibility = View.GONE
            return
        }

        binding.videoLoadingProgress.visibility = View.VISIBLE
        binding.btnPlayVideo.visibility = View.GONE
        
        try {
            val validUrl = if (videoUrl.startsWith("file:///android_asset/")) {
                // Copy asset to cache file because VideoView cannot play assets directly
                val assetPath = videoUrl.removePrefix("file:///android_asset/")
                val file = File(requireContext().cacheDir, File(assetPath).name)
                
                if (!file.exists()) {
                    try {
                        requireContext().assets.open(assetPath).use { inputStream ->
                            java.io.FileOutputStream(file).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.tvVideoError.visibility = View.VISIBLE
                        binding.tvVideoError.text = "Error loading video"
                        binding.videoLoadingProgress.visibility = View.GONE
                        return
                    }
                }
                file.absolutePath
            } else {
                videoUrl
            }

            binding.videoView.setVideoPath(validUrl)
            
            // Loop video
            binding.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                binding.videoLoadingProgress.visibility = View.GONE
                binding.btnPlayVideo.visibility = View.GONE
                // Auto play
                binding.videoView.start() 
            }
            
            binding.videoView.setOnErrorListener { _, _, _ ->
                binding.videoLoadingProgress.visibility = View.GONE
                binding.tvVideoError.visibility = View.VISIBLE
                binding.tvVideoError.text = "Error playing video"
                true
            }
            
            // Play/Pause on click
            binding.videoContainer.setOnClickListener {
                if (binding.videoView.isPlaying) {
                    binding.videoView.pause()
                    binding.btnPlayVideo.visibility = View.VISIBLE
                } else {
                    binding.videoView.start()
                    binding.btnPlayVideo.visibility = View.GONE
                }
            }
            
            binding.btnPlayVideo.setOnClickListener {
                binding.videoView.start()
                binding.btnPlayVideo.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvVideoError.visibility = View.VISIBLE
            binding.videoLoadingProgress.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.isPlaying) {
            binding.videoView.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
