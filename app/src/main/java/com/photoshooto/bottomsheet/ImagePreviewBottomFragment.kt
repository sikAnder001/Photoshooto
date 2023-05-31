package com.photoshooto.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.ImagepreviewBottomSheetBinding
import com.photoshooto.util.urlAddingForPicture

class ImagePreviewBottomFragment : BottomSheetDialogFragment() {
    val TAG = "ImagePreviewBottomFragment"
    private lateinit var binding: ImagepreviewBottomSheetBinding

    companion object {
        var title: String? = null
        var imagepath: String? = null
        fun newInstance(
            title: String, imagePath: String
        ): ImagePreviewBottomFragment {
            Companion.title = title
            Companion.imagepath = imagePath
            return ImagePreviewBottomFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImagepreviewBottomSheetBinding.inflate(inflater, container, false)
        return binding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = title

        binding.imgClose.setOnClickListener {
            dismiss()
        }
        Glide.with(requireActivity())
            .load(urlAddingForPicture(imagepath!!))
            .error(R.drawable.greenbutton_ripple)
            .into(binding.imagePreview)

    }
}
