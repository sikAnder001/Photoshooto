package com.photoshooto.ui.usedPhotographyScreens.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentFullScreenImageBinding
import com.photoshooto.ui.usedPhotographyScreens.adapter.FullScreenViewPagerAdapter


class FullScreenImageFragment : BaseFragment() {

    lateinit var binding: FragmentFullScreenImageBinding

    private lateinit var fullScreenViewPagerAdapter: FullScreenViewPagerAdapter
    private val navArgs: FullScreenImageFragmentArgs by navArgs()
    val imageList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentFullScreenImageBinding.inflate(inflater, container, false)



        binding.imgFullBack.setOnClickListener {
            findNavController().popBackStack()
        }

        imageList.add(navArgs.imageUrl)

        fullScreenViewPagerAdapter = FullScreenViewPagerAdapter()

        binding.vpFullImage.adapter = fullScreenViewPagerAdapter
        fullScreenViewPagerAdapter.swapList(imageList)

        /// binding.vpFullImage.currentItem = 0

        return binding.root
    }


}