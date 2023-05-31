package com.photoshooto.ui.landing_page

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.photoshooto.databinding.FragmentExoPlayerViewBinding

class ExoPlayerViewFragment : Fragment(), PlayerControlView.VisibilityListener {

    private var _binding: FragmentExoPlayerViewBinding? = null
    private val binding get() = _binding!!

    private var videoUrl: String? = null
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    companion object {
        private const val VIDEO_URL = "video_url"
        fun newInstance(videoUrl: String) = ExoPlayerViewFragment().apply {
            arguments = Bundle().apply {
                putString(VIDEO_URL, videoUrl)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoUrl = it.getString(VIDEO_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExoPlayerViewBinding.inflate(inflater, container, false)
//        initializeExoPlayer()
        return binding.root
    }

    private fun initializePlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(requireActivity())
        simpleExoPlayer.prepare(buildMediaSource(Uri.parse(videoUrl)))
        simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.seekTo(2000)
        /*   Handler(Looper.getMainLooper()).postDelayed({
               simpleExoPlayer.playWhenReady = false
           }, 2500)*/

//        binding.videoView.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.videoView.player = simpleExoPlayer
//        binding.videoView.hideController()
//        binding.videoView.useController = false
        binding.videoView.requestFocus()

    }

    //
    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(requireContext(), "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onVisibilityChange(visibility: Int) {
    }
}