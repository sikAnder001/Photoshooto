package com.photoshooto.ui.dashboard.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.photoshooto.databinding.FragmentHomeBinding
import com.photoshooto.util.PreferenceManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (PreferenceManager.saveNotifyDone == false) {
            binding.notifyLayout.visibility = View.VISIBLE
        } else {
            binding.notifyLayout.visibility = View.GONE
        }

        binding.btnNotify.setOnClickListener {
            NotifyMeDialog.newInstance("", requireContext())
                .show(getParentFragmentManager(), NotifyMeDialog.TAG)
            binding.notifyLayout.visibility = View.GONE
        }

//        btn_dashboard.setOnClickListener {
//            findNavController().navigate(
//                MainFragmentDirections.actionMainFragmentToDashboardFragment())
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}