package com.photoshooto.ui.admin_screen.manage_job_posting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.VMFactory
import com.photoshooto.api_call_package.view_model.JobsViewModel
import com.photoshooto.databinding.FragmentJobPostedBinding
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.showKeyboard


class ReportedJobFragment(val param: String) : Fragment() {
    lateinit var binding: FragmentJobPostedBinding

    private lateinit var repository: Repository2
    private lateinit var viewModel: JobsViewModel
    private lateinit var factory: VMFactory

    lateinit var reportedJobAdapter: ReportedJobAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobPostedBinding.inflate(inflater, container, false)

        repository = Repository2()
        factory = VMFactory(repository)
        viewModel = ViewModelProvider(this, factory)[JobsViewModel::class.java]

        showReportedJob()

        initObserver()

        return binding.root
    }

    private fun initObserver() {
        with(viewModel) {
            getReportedResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    reportedJobAdapter.setList(it.body()!!.data!!.list)
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }

            removeJobResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
                    if (requireContext().isInternetAvailable()) {
                        when (param) {
                            "Recently Updated" -> viewModel.getReportedSpam(10, 0, -1)
                            "Most Reported" -> viewModel.getReportedSpam(10, 0, 0)
                            else -> viewModel.getReportedSpam(10, 0, null)
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please check your internet",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    reportedJobAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun showReportedJob() {
        if (requireContext().isInternetAvailable()) {
            when (param) {
                "Recently Updated" -> viewModel.getReportedSpam(10, 0, 1)
                "Most Reported" -> viewModel.getReportedSpam(10, 0, -1)
                else -> viewModel.getReportedSpam(10, 0, -1)
            }

        } else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }
        reportedJobAdapter =
            ReportedJobAdapter(context, object : ReportedJobAdapter.SelectedDeleteInterface {
                override fun onClick(data: String) {
                    var dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.dialog_cancel_user)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.show()
                }
            },
                object : ReportedJobAdapter.ViewDetailsInterface {
                    override fun onClick(data: String, id: String) {
                        var bundle = Bundle()
                        bundle.putString("btnText", data)
                        bundle.putString("id", id)
                        view!!.findNavController().navigate(R.id.aboutFragment, bundle)
                    }

                },
                object : ReportedJobAdapter.RemoveInterface {
                    override fun onClick(data: String, id: String) {
                        removePost(id)
                    }

                }

            )

        binding.rvJobPosted.adapter = reportedJobAdapter

//        reportedJobAdapter.setList(dataFun())

    }

    private fun removePost(id: String) {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_decline_post)

        val btnDecline = dialog.findViewById(R.id.btnDecline) as Button
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton
        val rgDecline = dialog.findViewById(R.id.rgDecline) as RadioGroup
        val etOther = dialog.findViewById(R.id.etOther) as EditText
        val tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle) as TextView
        val tvDesc = dialog.findViewById(R.id.tvDesc) as TextView

        tvDialogTitle.text = getString(R.string.remove_Post)
        tvDesc.text = getString(R.string.select_reason_for_remove_post)
        btnDecline.text = getString(R.string.remove)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDecline.setOnClickListener {
            dialog.dismiss()
            if (requireContext().isInternetAvailable()) {
                viewModel.removeJob(id)
            } else {
                Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        rgDecline.setOnCheckedChangeListener { group, checkedId ->
            requireContext().hideKeyboardFrom(etOther)
            etOther.isEnabled = false
            etOther.setText("")
            etOther.setHint(getString(R.string.others))
            rdOther.isChecked = false
        }

        rdOther.setOnCheckedChangeListener { compoundButton, check ->
            if (check && compoundButton.isPressed) {
                rgDecline.clearCheck()
                etOther.isEnabled = true
                etOther.setText("")
                etOther.requestFocus()
                etOther.showKeyboard()
                rdOther.isChecked = true
            }
        }

        dialog.show()
    }

    private fun dataFun(): ArrayList<String> {
        var data = ArrayList<String>()
        data.add("Shiv")
        data.add("Shubham")
        data.add("Rohit")
        return data
    }
}