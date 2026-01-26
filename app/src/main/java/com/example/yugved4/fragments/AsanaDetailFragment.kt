package com.example.yugved4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.yugved4.R
import com.example.yugved4.database.DatabaseHelper
import com.google.android.material.chip.Chip

/**
 * Fragment displaying detailed information about a specific yoga asana
 * Updated to use SQL database instead of YogaDataProvider
 */
class AsanaDetailFragment : Fragment() {

    private lateinit var tvSanskritName: TextView
    private lateinit var tvEnglishName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvBenefits: TextView
    private lateinit var chipDuration: Chip
    private lateinit var chipDifficulty: Chip
    private lateinit var chipCategory: Chip
    
    // Video Views
    private lateinit var videoView: android.widget.VideoView
    private lateinit var btnPlayVideo: android.widget.ImageView
    private lateinit var videoLoadingProgress: android.widget.ProgressBar
    private lateinit var tvVideoError: TextView
    private lateinit var imgPlaceholder: android.widget.ImageView
    private lateinit var videoContainer: android.widget.FrameLayout
    
    private lateinit var dbHelper: DatabaseHelper
    
    private var asanaId: Int = 0
    private var asana: DatabaseHelper.YogaAsana? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_asana_detail, container, false)
        
        // Get asana ID from arguments (now expecting integer)
        asanaId = arguments?.getInt("asanaId") ?: 0
        
        // Initialize views
        tvSanskritName = view.findViewById(R.id.tvSanskritName)
        tvEnglishName = view.findViewById(R.id.tvEnglishName)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvBenefits = view.findViewById(R.id.tvBenefits)
        chipDuration = view.findViewById(R.id.chipDuration)
        chipDifficulty = view.findViewById(R.id.chipDifficulty)
        chipCategory = view.findViewById(R.id.chipCategory)
        
        // Initialize Video Views
        videoView = view.findViewById(R.id.videoView)
        btnPlayVideo = view.findViewById(R.id.btnPlayVideo)
        videoLoadingProgress = view.findViewById(R.id.videoLoadingProgress)
        tvVideoError = view.findViewById(R.id.tvVideoError)
        imgPlaceholder = view.findViewById(R.id.imgPlaceholder)
        videoContainer = view.findViewById(R.id.videoContainer)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dbHelper = DatabaseHelper(requireContext())
        
        loadAsanaData()
        displayAsanaDetails()
    }

    /**
     * Load asana data from database by ID
     */
    private fun loadAsanaData() {
        // Fetch from database using SQL query
        asana = dbHelper.getAsanaById(asanaId)
    }

    /**
     * Display asana details in UI
     */
    private fun displayAsanaDetails() {
        asana?.let { yogaAsana ->
            // Display names
            tvSanskritName.text = yogaAsana.sanskritName
            tvEnglishName.text = yogaAsana.name
            
            // Display description
            tvDescription.text = yogaAsana.description
            
            // Display metadata
            chipDuration.text = yogaAsana.duration
            chipDifficulty.text = yogaAsana.difficultyLevel
            chipCategory.text = yogaAsana.category
            
            // Display benefits as bulleted list
            val benefitsText = yogaAsana.benefits.joinToString("\n") { "• $it" }
            tvBenefits.text = benefitsText
            
            // Setup Video Player
            setupVideoPlayer(yogaAsana.videoUrl)
        }
    }
    
    private fun setupVideoPlayer(videoUrl: String?) {
        if (videoUrl.isNullOrEmpty()) {
            tvVideoError.visibility = View.VISIBLE
            imgPlaceholder.visibility = View.VISIBLE
            videoLoadingProgress.visibility = View.GONE
            return
        }

        videoLoadingProgress.visibility = View.VISIBLE
        btnPlayVideo.visibility = View.GONE
        imgPlaceholder.visibility = View.GONE
        
        try {
            val validUrl = if (videoUrl.startsWith("file:///android_asset/")) {
                // Copy asset to cache file
                val assetPath = videoUrl.removePrefix("file:///android_asset/")
                val file = java.io.File(requireContext().cacheDir, java.io.File(assetPath).name)
                
                if (!file.exists()) {
                    try {
                        requireContext().assets.open(assetPath).use { inputStream ->
                            java.io.FileOutputStream(file).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        tvVideoError.visibility = View.VISIBLE
                        tvVideoError.text = "Error loading video"
                        videoLoadingProgress.visibility = View.GONE
                        return
                    }
                }
                file.absolutePath
            } else {
                videoUrl
            }

            videoView.setVideoPath(validUrl)
            
            // Loop video
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                videoLoadingProgress.visibility = View.GONE
                btnPlayVideo.visibility = View.GONE
                // Auto play
                videoView.start() 
            }
            
            videoView.setOnErrorListener { _, _, _ ->
                videoLoadingProgress.visibility = View.GONE
                tvVideoError.visibility = View.VISIBLE
                tvVideoError.text = "Error playing video"
                true
            }
            
            // Play/Pause on click
            videoContainer.setOnClickListener {
                if (videoView.isPlaying) {
                    videoView.pause()
                    btnPlayVideo.visibility = View.VISIBLE
                } else {
                    videoView.start()
                    btnPlayVideo.visibility = View.GONE
                }
            }
            
            btnPlayVideo.setOnClickListener {
                videoView.start()
                btnPlayVideo.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
            tvVideoError.visibility = View.VISIBLE
            videoLoadingProgress.visibility = View.GONE
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (::videoView.isInitialized && videoView.isPlaying) {
            videoView.pause()
        }
    }
}
