package com.photoshooto.ui.userhomepage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.photoshooto.databinding.FragmentTopPhotographerServiceBinding
import com.photoshooto.domain.model.PhotographerServiceModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.userhomepage.adapters.PhotographyServiceAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class TopPhotographerServiceFragment : Fragment() {

    private lateinit var binding: FragmentTopPhotographerServiceBinding
    private val viewModel: GetUserProfileViewModel by viewModel()
    var list: List<PhotographerServiceModel.Data?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopPhotographerServiceBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPhotographerServices()
        initObserver()
    }

    private fun initObserver() {
        viewModel.photographerServiceResponse.observe(requireActivity()) {
            if (it.success!!) {
                val data = it.data
                if (!data.isNullOrEmpty()) list = data
                binding.recyclerViewPhotographyServices.adapter =
                    PhotographyServiceAdapter(requireActivity(), list)
            }
        }

        viewModel.showProgressbar.observe(requireActivity(), Observer {
            binding.progressBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    fun filter(text: String?) {
        val temp: MutableList<PhotographerServiceModel.Data?> = ArrayList()
        if (list.isNotEmpty()) {
            if (!text.isNullOrEmpty()) {
                for (services in list) {
                    if (services?.type?.lowercase(Locale.getDefault())!!
                            .contains(text.lowercase(Locale.getDefault()))
                    ) {
                        temp.add(services)
                    }
                }
            } else {
                temp.addAll(list)
            }
        }

        //update recyclerview
        binding.recyclerViewPhotographyServices.adapter =
            PhotographyServiceAdapter(requireActivity(), temp)
    }

}