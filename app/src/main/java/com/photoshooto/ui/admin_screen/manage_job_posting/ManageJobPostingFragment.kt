package com.photoshooto.ui.admin_screen.manage_job_posting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.databinding.FragmentManageJobPostingBinding
import com.photoshooto.domain.model.User

class ManageJobPostingFragment : Fragment() {
    private lateinit var binding: FragmentManageJobPostingBinding

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageJobPostingBinding.inflate(inflater, container, false)

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        openTabs("")

        binding.imgSort.setOnClickListener {

            openSort()
        }


        return binding.root
    }

    private fun openSort() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_sort_by)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rgGroup = dialog.findViewById(R.id.rdGroup) as RadioGroup
        val btnApply = dialog.findViewById(R.id.btnApply) as Button

        btnApply.setOnClickListener {
            var id: Int = rgGroup.checkedRadioButtonId
            if (id != -1) {
                val radio: RadioButton = dialog.findViewById(id)
                openTabs(radio.text as String)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun openTabs(param: String) {

        binding.vpTab.adapter = JobTabViewPagerAdapter(childFragmentManager, lifecycle, param)
        TabLayoutMediator(binding.tbLayout, binding.vpTab) { tab, position ->
            tab.text =
                arrayOf(getString(R.string.job_posted), getString(R.string.reported_jobs))[position]
        }.attach()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}