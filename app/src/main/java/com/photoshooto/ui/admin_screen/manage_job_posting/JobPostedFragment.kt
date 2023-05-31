package com.photoshooto.ui.admin_screen.manage_job_posting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.VMFactory
import com.photoshooto.api_call_package.view_model.JobsViewModel
import com.photoshooto.databinding.FragmentJobPostedBinding
import com.photoshooto.domain.model.ApproveRejectBody
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.showKeyboard


class JobPostedFragment(val param: String) : Fragment() {
    lateinit var binding: FragmentJobPostedBinding

    private lateinit var repository: Repository2
    private lateinit var viewModel: JobsViewModel
    private lateinit var factory: VMFactory

    lateinit var jobPostedAdapter: JobPostedAdapter
    lateinit var jobs: ArrayList<String>
    var idForToast = ""
    var approveOrReject = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobPostedBinding.inflate(inflater, container, false)

        repository = Repository2()

        factory = VMFactory(repository)

        viewModel = ViewModelProvider(this, factory)[JobsViewModel::class.java]

        showJobsPosted()

        initObserver()
        return binding.root
    }

    private fun initObserver() {
        with(viewModel) {
            getJobsResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    jobPostedAdapter.setList(it.body()!!.data!!.list)
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }

            approveDeclineResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message

                    val inflater = layoutInflater
                    val layout: View = inflater.inflate(
                        R.layout.custom_toast,
                        view!!.findViewById(R.id.toast_layout_root) as ViewGroup?
                    )


                    val approveDeclineTv =
                        layout.findViewById<View>(R.id.approveOrDeclineTv) as TextView
                    val toastId = layout.findViewById<View>(R.id.toastId) as TextView
                    val imgView = layout.findViewById<View>(R.id.image) as ImageView
                    approveDeclineTv.text = approveOrReject
                    toastId.text = idForToast

                    if (approveOrReject == "Job Decline Id") {
                        layout.setBackgroundResource(R.drawable.orange_text_bg)
                        imgView.setImageResource(R.drawable.orange_tick)
                        toastId.setTextColor(ContextCompat.getColor(context!!, R.color.orange_clr))
                    } else {
                        layout.setBackgroundResource(R.drawable.light_green_back)
                        imgView.setImageResource(R.drawable.ic_green_tick_circle)
                        toastId.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.green_status
                            )
                        )
                    }

                    val toast = Toast(requireActivity().applicationContext)
                    toast.setGravity(Gravity.BOTTOM, 0, 0)
                    toast.duration = Toast.LENGTH_LONG
                    toast.setView(layout)
                    toast.show()

                    when (param) {
                        "Recently Updated" -> viewModel.getJobs(50, -1, "")
                        "Most Reported" -> viewModel.getJobs(50, 0, "created_at")
                        else -> viewModel.getJobs(50, null, "")
                    }
                    jobPostedAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showJobsPosted() {
        if (requireContext().isInternetAvailable()) {
            when (param) {
                "Recently Updated" -> viewModel.getJobs(50, -1, "")
                "Most Reported" -> viewModel.getJobs(50, 0, "created_at")
                else -> viewModel.getJobs(50, null, "")
            }
        } else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }
        jobPostedAdapter =
            JobPostedAdapter(context, object : JobPostedAdapter.SelectedDeleteInterface {
                override fun onClick(data: String) {
                    var dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.dialog_cancel_user)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.show()
                }
            },
                object : JobPostedAdapter.ViewDetailsInterface {
                    override fun onClick(data: String, id: String) {
                        var bundle = Bundle()
                        bundle.putString("btnText", data)
                        bundle.putString("id", id)
                        view!!.findNavController().navigate(R.id.aboutFragment, bundle)
                    }
                },
                object : JobPostedAdapter.ApproveInterface {
                    override fun onClick(data: String, id: String) {
                        approveOrReject = "Job Approved Id"
                        idForToast = ": $id"
                        jobs = ArrayList()
                        jobs.clear()
                        jobs.add(id)
                        viewModel.approveDeclineStatus(
                            "approved",
                            ApproveRejectBody(jobs = jobs, "")
                        )
                    }
                },
                object : JobPostedAdapter.DeclineInterface {
                    override fun onClick(data: String, id: String) {
                        approveOrReject = "Job Decline Id"
                        idForToast = ": $id"
                        declinePost(id)
                    }
                }
            )

        binding.rvJobPosted.adapter = jobPostedAdapter

    }

    private fun declinePost(id: String) {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_decline_post)

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnDecline = dialog.findViewById(R.id.btnDecline) as Button
        val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton
        val rgDecline = dialog.findViewById(R.id.rgDecline) as RadioGroup
        val etOther = dialog.findViewById(R.id.etOther) as EditText
        val tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle) as TextView

        jobs = ArrayList()
        jobs.clear()
        jobs.add(id)

        btnCancel.setOnClickListener {

            dialog.dismiss()
        }

        rgDecline.setOnCheckedChangeListener { group, checkedId ->
            requireContext().hideKeyboardFrom(etOther)
            etOther.isEnabled = false
            etOther.setText("")
            etOther.hint = getString(R.string.others)
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

        btnDecline.setOnClickListener {
            var id: Int = rgDecline.checkedRadioButtonId
            if (rdOther.isChecked) {
                if (etOther.text.isEmpty())
                    Toast.makeText(
                        context, "please provide reason",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    viewModel.approveDeclineStatus(
                        "rejected", ApproveRejectBody(
                            jobs = jobs,
                            etOther.text as String?
                        )
                    )
                    dialog.dismiss()
                }
            } else if (id != -1) {
                val radio: RadioButton = dialog.findViewById(id)
                viewModel.approveDeclineStatus(
                    "rejected", ApproveRejectBody(
                        jobs = jobs,
                        radio.text as String?
                    )
                )
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /*private fun dataFun(): ArrayList<String> {
        var data = ArrayList<String>()
        data.add("Shiv")
        data.add("Shubham")
        data.add("Rohit")
        return data
    }*/
}