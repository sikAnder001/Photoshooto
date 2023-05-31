package com.photoshooto.ui.dashboard.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {


//    val bundle = bundleOf("selectedUser_type" to selectedUser_type)
//val direction =
//    FragmentRoutingDirections.actionFragmentRoutingToLoginFragment(selectedUser_type)
//    findNavController().navigate(direction)

    val setType = MutableLiveData<String>()
    fun setType(data: String) {
        setType.value = data
    }
//    homeViewModel?.message?.observe(viewLifecycleOwner, Observer {
//        binding.contactDetailsTxt.text = it
//        Toast.makeText(requireActivity(), "setUserType "+it, Toast.LENGTH_SHORT).show()
//    })
//    homeViewModel?.sendMessage(selectedUser_type)

}