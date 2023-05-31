package com.photoshooto.ui.admin_screen.manage_job_posting

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.VMFactory
import com.photoshooto.api_call_package.view_model.JobsViewModel
import com.photoshooto.databinding.FragmentAboutBinding
import com.photoshooto.domain.model.ApproveRejectBody
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.showKeyboard
import com.photoshooto.util.urlAddingForPicture
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class AboutFragment : Fragment() {

    lateinit var binding: FragmentAboutBinding

    private lateinit var repository: Repository2
    private lateinit var viewModel: JobsViewModel
    private lateinit var factory: VMFactory

    lateinit var jobs: ArrayList<String>

    var btnText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)

        repository = Repository2()
        factory = VMFactory(repository)
        viewModel = ViewModelProvider(this, factory)[JobsViewModel::class.java]

        btnText = arguments!!.getString("btnText").toString()
        var id = arguments!!.getString("id")

        if (btnText == "Remove") {
            binding.btnRemove.visibility = View.VISIBLE
            binding.layoutBtnAppDec.visibility = View.GONE
        }

        binding.btnApproval.setOnClickListener {
            jobs = ArrayList()
            jobs.clear()
            jobs.add(id.toString())
            viewModel.approveDeclineStatus(
                "approved",
                ApproveRejectBody(jobs = jobs, "")
            )
        }

        binding.btnDecline.setOnClickListener {
            declinePost(id.toString())
        }

        binding.btnRemove.setOnClickListener {
            removePost(id.toString())
        }

        getJobById(id)
        initObserver()

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        binding.shareTv.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage = ""
                shareMessage = """${shareMessage + "www.photoshooto.com/$btnText/${id}"}
                            """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                context!!.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {
                //e.toString();
            }
        }


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initObserver() {
        try {
            viewModel.getJobByIdResponse.observe(this) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.data!!
                    binding.apply {
                        if (data.isEmpty()) {
                            Toast.makeText(context, "No Data Found!", Toast.LENGTH_SHORT).show()
                        } else {
                            if (data[0].userProfile!!.backgroundImage != null) {
                                Glide.with(context!!)
                                    .load(urlAddingForPicture(data[0].userProfile!!.backgroundImage!!))
                                    .error(R.drawable.ic_profile_placeholder).into(imageView)
                            }

                            var sDate = ""
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                sDate =
                                    Instant                           // Represents a point on the time line.
                                        .parse(data[0].startDate)  // Returns an `Instant` object. By default, parses text in standard ISO 8601 for at where `Z` means offset of zero.
                                        .atOffset(ZoneOffset.UTC)       // Returns an `OffsetDateTime` object.
                                        .format(
                                            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                        )
                            }

                            eventStartTimeTv.text = "${sDate}, ${data[0].startTime}"

                            var lDate = ""
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                lDate =
                                    Instant                           // Represents a point on the time line.
                                        .parse(data[0].endDate)  // Returns an `Instant` object. By default, parses text in standard ISO 8601 for at where `Z` means offset of zero.
                                        .atOffset(ZoneOffset.UTC)       // Returns an `OffsetDateTime` object.
                                        .format(
                                            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                        )
                            }


//                            Toast.makeText(context, "$sDate--$lDate", Toast.LENGTH_SHORT).show()
                            eventEndTimeTv.text = "${lDate}, ${data[0].endTime}"


                            if (data[0].status == "approved" || data[0].status == "rejected") {
                                layoutBtnAppDec.visibility = View.GONE
                            }

                            jobTitle.text = data[0].userProfile!!.studioName
                            jobId.text = data[0].id
                            addressTv.text = data[0].userProfile?.address ?: ""
                            ratingStar.text = data[0].userProfile!!.rating.toString()
                            ratingNum.text = "${data[0].feedback.size} ratings"
                            eventNameTv.text = data[0].title
                            eventTypeTv.text = data[0].eventType
                            eventLocationTv.text = data[0].address?.location ?: ""
                            /* eventStartTimeTv.text=data[0].startDate
                             eventEndTimeTv.text=data[0].startDate*/
                            photographyTypeTv.text = data[0].photographyType
                            var cam = StringJoiner("\n")

                            for (element in data[0].photoCameraUse) {
                                if (!element.isNullOrEmpty()) {
                                    cam.add(element)
                                }
                            }
                            cameraRequiredTv.text = cam.toString()

                            var vid = StringJoiner("\n")

                            for (element in data[0].videoCameraUse) {
                                if (!element.isNullOrEmpty()) {
                                    vid.add(element)
                                }
                            }
                            videoCamsTv.text = vid.toString()

                            var equip = StringJoiner("\n")

                            for (element in data[0].otherEquipments) {
                                if (!element.isNullOrEmpty()) {
                                    equip.add(element)
                                }
                            }
                            equipmentRequiredTv.text = equip.toString()

                            numOfPhotographerTv.text = data[0].numberOfPhotographers.toString()
                            descriptionTv.text = data[0].description
                        }
                    }
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

        try {
            viewModel.approveDeclineResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    Toast.makeText(context, it.body()!!.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

        try {
            viewModel.removeJobResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {

        }

    }

    private fun getJobById(id: String?) {
        if (requireContext().isInternetAvailable()) {
            viewModel.getJobById(id)
        } else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }
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


}