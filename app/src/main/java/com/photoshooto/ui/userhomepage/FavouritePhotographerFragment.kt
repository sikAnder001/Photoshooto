package com.photoshooto.ui.userhomepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.databinding.FragmentFavouritePhotographerBinding
import com.photoshooto.domain.model.UserElement
import com.photoshooto.ui.userhomepage.adapters.FavouritePhotographerListAdapter

class FavouritePhotographerFragment : Fragment(),
    FavouritePhotographerListAdapter.OnUserItemClickListener {

    private lateinit var binding: FragmentFavouritePhotographerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritePhotographerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarWithBack
        val backBtn = toolbar.backBtn
        val titleTxt = toolbar.tvTitle

        titleTxt.text = "Favorite Photographers"

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.recyclerViewPhotographerList.adapter =
            FavouritePhotographerListAdapter(requireActivity(), this)

    }

    override fun onEnquireClicked(user: UserElement) {

    }

    override fun onViewProfileClicked(user: UserElement) {

    }

    override fun onUserFavProfileClicked(user: UserElement, isFav: Boolean) {

    }

}