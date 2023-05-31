package com.photoshooto.ui.job.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentJobDetailBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.ui.dialog.ErrorDialog
import com.photoshooto.ui.dialog.ReportDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobDetailFragment : BaseFragment() {

    lateinit var binding: FragmentJobDetailBinding
    lateinit var mContext: Context
    private var isSelfView = false
    private var jobName = ""
    private var favId = ""
    private var jobId = ""
    private var latitude = "0"
    private var longitude = "0"
    private var isBookmarked = false
    lateinit var jobModel: JobModel
    lateinit var params2: ConstraintLayout.LayoutParams

    private val navArgs: JobDetailFragmentArgs by navArgs()
    private val jobHomeViewModel: JobViewModel by viewModel()
    lateinit var toolbarTitleView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJobDetailBinding.inflate(inflater, container, false)
        val toolbarBinding = binding.toolbarAboutJobDetails
        toolbarTitleView = toolbarBinding.tvTitle
        toolbarBinding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()

        binding.studioName.text = ""
        binding.userId.text = ""
        binding.city.text = ""
        binding.adId.text = ""
        binding.time.text = ""

        attachObserver()

        jobId = navArgs.jobId

        binding.edit.setSafeOnClickListener {
            AppConstant.tmpJobModel = jobModel
            findNavController().navigate(
                JobDetailFragmentDirections.actionJobDeatilToEditJob(jobModel.type)
            )
        }

    }

    private fun attachObserver() {

        jobHomeViewModel.jobById.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                if (it.list.isEmpty()) {
                    ErrorDialog.showDialog(
                        requireContext(),
                        getString(R.string.invalid_job),
                        getString(R.string.okay), false
                    ) {
                        findNavController().navigateUp()
                    }
                } else {
                    it.list.first { model ->
                        jobModel = model
                        binding.scroll.visible()

                        isSelfView =
                            jobModel.userId.equals(
                                SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
                                true
                            )
                        if (isSelfView) {
                            binding.selfGroup.gone()
                            binding.edit.gone()
                            binding.btnContact.gone()
                        }

                        if (jobModel.type.equals(
                                "hireyou",
                                true
                            )
                        ) {
                            //hireyou
                            toolbarTitleView.text = "About the Job"
                            displayPageAsHireYou()
                        } else {
                            //hireme
                            toolbarTitleView.text = "About the Photographer"
                            displayPageAsHireMe()
                        }
                        true
                    }
                }

            } else {
                requireContext().toastError(it.message.toString())
            }
        }

        jobHomeViewModel.spamReport.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }

        jobHomeViewModel.saveFavourite.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                favId = ""
                binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)
                jobModel.isFavorite = true
                isBookmarked = true

                favId = it.data.id
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }

        jobHomeViewModel.removeFavourite.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                binding.bookmark.setImageResource(R.drawable.ic_bookmark)
                jobModel.isFavorite = false
                isBookmarked = false

                favId = ""
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }
    }

    private fun displayPageAsHireYou() {
        //page setup
        binding.apply {

            adId.text = "AD ID: ${jobId}"
            time.text = "Posted: " + jobModel.createdAt.getDateTimeDiffSingle()
            rating.text =
                jobModel.userProfile.rating?.singleDecimalPlaces()
            ratingCount.text = "(${jobModel.feedbacks.size} rating)"
            city.text = jobModel.userProfile.city
            userId.text = "ID: ${jobModel.userId}"
            studioName.text = jobModel.userProfile.studio_name

            jobModel.userProfile.profile_image?.let {
                loadImageUser(
                    binding.imageViewProfile,
                    it
                )
            }

            if (jobModel.userProfile.online_status.equals("online", true)) {
                dot.visible()
            } else {
                dot.invisible()
            }

            jobName =
                (jobModel.title ?: "").ifBlank { jobModel.eventType.toString() }
            latitude = jobModel.addressObj.getLatitudeAddress()
            longitude = jobModel.addressObj.getLongitudeAddress()

            verify.isVisible = jobModel.isVerified
            isBookmarked = jobModel.isFavorite

            if (jobModel.isFavorite) {
                favId = jobModel.favorite[0].id
                isBookmarked = true
                binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                favId = ""
                isBookmarked = false
                binding.bookmark.setImageResource(R.drawable.ic_bookmark)
            }

            binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            report.setSafeOnClickListener {
                val nameTitle = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(requireContext(), R.color.colorOrange)
                        ds.isUnderlineText = false
                    }
                }

                val radioList = arrayListOf<String>()
                radioList.addAll(resources.getStringArray(R.array.report_job))

                val tmp = "Report Job, ${jobName}"

                val spannableString = SpannableString(tmp)
                spannableString.setSpan(
                    nameTitle, 12, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                ReportDialog.showDialog(
                    requireContext(), spannableString, radioList
                ) { message ->
                    jobHomeViewModel.spamReport(navArgs.jobId, message, "job")

                }
            }

            btnContact.setSafeOnClickListener {
                if (jobModel.mobile.isNotEmpty()) {
                    try {
                        jobModel.mobile.first().toString().callFromDialer(requireContext())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            eventNameRow.label1.text = getString(R.string.event_name)
            eventNameRow.value1.setTextColor(mContext.resources.getColor(R.color.colorOrange))
            eventNameRow.value1.text = (jobModel.title ?: "").ifBlank {
                jobModel.eventType
            }
            eventNameRow.img1.setImageResource(R.drawable.job_event)
            eventNameRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorOrangeDull)


            eventTypeRow.label1.text = getString(R.string.event_tp)
            eventTypeRow.value1.setTextColor(mContext.resources.getColor(R.color.colorBlueText))
            eventTypeRow.value1.text = jobModel.eventType
            eventTypeRow.img1.setImageResource(R.drawable.ic_event_megaphone)
            eventTypeRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorBlueDull)


            eventDescRow.label1.text = getString(R.string.description)
            eventDescRow.value1.setTextColor(mContext.resources.getColor(R.color.grey_66))
            eventDescRow.value1.text = jobModel.description
            eventDescRow.img1.setImageResource(R.drawable.ic_event_desc)
            eventDescRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.grey_ec)

            eventLocationRow.label1.text = getString(R.string.event_location)
            eventLocationRow.value1.setTextColor(mContext.resources.getColor(R.color.colorPinkText))
            eventLocationRow.actionLabel.visible()
            eventLocationRow.value1.text = jobModel.addressObj.parseAddress()
            eventLocationRow.img1.setImageResource(R.drawable.ic_event_location)
            eventLocationRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorPinkDull)
            eventLocationRow.actionLabel.setSafeOnClickListener {
                val mapUri = Uri.parse("geo:0,0?q=${latitude},${longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }

            eventDateRow.secondRowGrup.visible()
            eventDateRow.label1.text = getString(R.string.event_first_time)
            eventDateRow.value1.setTextColor(mContext.resources.getColor(R.color.colorGreenText))
            eventDateRow.value1.text = "From: ${
                (jobModel.startDate).getFormattedDatetime(
                    outputFormat = "dd-MM-yyyy"
                )
            }, ${jobModel.startTime}"
            eventDateRow.dottedLIne.visible()
            eventDateRow.img1.setImageResource(R.drawable.ic_event_clock)


            val startDate = "${
                (jobModel.startDate).getFormattedDatetime(
                    outputFormat = "dd-MM-yyyy"
                )
            } ${jobModel.startTime}"

            val endDate = "${
                (jobModel.endDate).getFormattedDatetime(
                    outputFormat = "dd-MM-yyyy"
                )
            } ${jobModel.endTime}"

            val dateDiff = startDate.lowercase().getDateTimeDiff(
                endDate = endDate.lowercase(),
                inputFormat = "dd-MM-yyyy hh:mma"
            )

            eventDateRow.actionLabel.visible()
            eventDateRow.actionLabel.text = dateDiff
            eventDateRow.label2.text = getString(R.string.event_last_time)
            eventDateRow.value2.setTextColor(mContext.resources.getColor(R.color.colorGreenText))
            eventDateRow.actionLabel.setTextColor(mContext.resources.getColor(R.color.colorGreenText))
            eventDateRow.value2.text = "To: ${
                (jobModel.endDate).getFormattedDatetime(
                    outputFormat = "dd-MM-yyyy"
                )
            }, ${jobModel.endTime}"
            eventDateRow.img2.setImageResource(R.drawable.ic_event_clock)
            eventDateRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorGreenDull)
            eventDateRow.actionLabel.setCompoundDrawables(null, null, null, null)

            params2 = eventDateRow.viewEvent.layoutParams as ConstraintLayout.LayoutParams
            params2.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            params2.bottomToBottom = eventDateRow.space2.id

            eventPhotographyType.label1.text = getString(R.string.photography_type)
            eventPhotographyType.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventPhotographyType.value1.text = jobModel.photographyType
            eventPhotographyType.img1.setImageResource(R.drawable.ic_event_cam)

            eventCameraRequired.label1.text = getString(R.string.camera_required)
            eventCameraRequired.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventCameraRequired.value1.text = jobModel.photoCameraUse.listToBreakables()
            eventCameraRequired.img1.setImageResource(R.drawable.ic_event_cam)

            eventVideoRequired.label1.text = getString(R.string.video_cam_required)
            eventVideoRequired.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventVideoRequired.value1.text = jobModel.videoCameraUse.listToBreakables()
            eventVideoRequired.img1.setImageResource(R.drawable.ic_event_video)

            eventEquipmentRequired.label1.text = getString(R.string.equipments_required)
            eventEquipmentRequired.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventEquipmentRequired.value1.text = jobModel.otherEquipments.listToBreakables()
            eventEquipmentRequired.img1.setImageResource(R.drawable.ic_event_equipment)

            eventPhotographerCount.label1.text = getString(R.string.number_of_photographers)
            eventPhotographerCount.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventPhotographerCount.value1.text = if (jobModel.numberOfPhotographers > 1) {
                "${jobModel.numberOfPhotographers} Photographers"
            } else {
                "${jobModel.numberOfPhotographers} Photographer"
            }

            eventPhotographerCount.img1.setImageResource(R.drawable.ic_enevt_photographer_cnt)

            bookmark.setSafeOnClickListener {
                if (!isBookmarked) {
                    showProgress()
                    jobHomeViewModel.saveFavourite(jobId)
                } else {
                    showProgress()
                    jobHomeViewModel.removeFavourite(favId)
                }
            }

            share.setSafeOnClickListener {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                    var shareMessage = ""
                    shareMessage =
                        """${shareMessage + "www.photoshooto.com/JobListing/${jobId}"}
                            """.trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    context!!.startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: java.lang.Exception) {
                    //e.toString();
                }
            }
        }
    }

    private fun displayPageAsHireMe() {
        //page setup
        binding.apply {

            adId.text = "AD ID: ${jobId}"
            time.text = "Posted : " + jobModel.createdAt.getDateTimeDiffSingle()
            rating.text =
                jobModel.userProfile.rating?.singleDecimalPlaces()
            ratingCount.text = "(${jobModel.feedbacks.size} rating)"
            city.text = jobModel.userProfile.city
            userId.text = "ID: ${jobModel.userId}"
            studioName.text = jobModel.userProfile.name

            ratingBack.setSafeOnClickListener {
                findNavController().navigate(
                    JobDetailFragmentDirections.actionJobDetailToReviewFragment(
                        jobModel.userId,
                        binding.check.isVisible,
                        jobModel.userProfile.name.toString(),
                        jobModel.userProfile.profile_image!!,
                        jobModel.userProfile.city!!
                    )
                )
            }

            rating.setSafeOnClickListener {
                ratingBack.callOnClick()
            }

            ratingCount.setSafeOnClickListener {
                ratingBack.callOnClick()
            }

            jobModel.userProfile.profile_image?.let {
                loadImageUser(
                    binding.imageViewProfile,
                    it
                )
            }

            ratingBack.visible()
            rating.setTextColor(Color.WHITE)
            ratingCount.setTextColor(Color.WHITE)

            if (jobModel.userProfile.online_status.equals("online", true)) {
                dot.visible()
            } else {
                dot.invisible()
            }

            jobName = jobModel.userProfile.name.toString()
            latitude = jobModel.addressObj.getLatitudeAddress()
            longitude = jobModel.addressObj.getLongitudeAddress()

            verify.setImageResource(R.drawable.ic_verifed_profile)
            verify.isVisible = jobModel.isVerified
            isBookmarked = jobModel.isFavorite

            if (jobModel.isFavorite) {
                favId = jobModel.favorite[0].id
                isBookmarked = true
                binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                favId = ""
                isBookmarked = false
                binding.bookmark.setImageResource(R.drawable.ic_bookmark)
            }

            binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            report.setSafeOnClickListener {
                val nameTitle = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(requireContext(), R.color.colorOrange)
                        ds.isUnderlineText = false
                    }
                }

                val radioList = arrayListOf<String>()
                radioList.addAll(resources.getStringArray(R.array.report_user))

                val tmp = "Report User, ${jobName}"

                val spannableString = SpannableString(tmp)
                spannableString.setSpan(
                    nameTitle, 12, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                ReportDialog.showDialog(
                    requireContext(), spannableString, radioList
                ) { message ->
                    jobHomeViewModel.spamReport(navArgs.jobId, message, "user")
                }
            }

            btnContact.setSafeOnClickListener {
                if (jobModel.mobile.isNotEmpty()) {
                    try {
                        jobModel.mobile.first().toString().callFromDialer(requireContext())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            eventNameRow.root.gone()

            eventTypeRow.label1.text = getString(R.string.event_of_type)
            eventTypeRow.value1.setTextColor(mContext.resources.getColor(R.color.colorBlueText))
            eventTypeRow.value1.text = jobModel.eventType
            eventTypeRow.img1.setImageResource(R.drawable.ic_event_megaphone)
            eventTypeRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorBlueDull)


            eventDescRow.label1.text = getString(R.string.description)
            eventDescRow.value1.setTextColor(mContext.resources.getColor(R.color.grey_66))
            eventDescRow.value1.text = jobModel.description
            eventDescRow.img1.setImageResource(R.drawable.ic_event_desc)
            eventDescRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.grey_ec)

            eventLocationRow.label1.text = getString(R.string.event_location)
            eventLocationRow.value1.setTextColor(mContext.resources.getColor(R.color.colorPinkText))
            eventLocationRow.value1.text =
                jobModel.city.toString().replace("]", "").replace("[", "")

            eventLocationRow.img1.setImageResource(R.drawable.ic_event_location)
            eventLocationRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorPinkDull)

            eventDateRow.label1.text = getString(R.string.available_dates)
            eventDateRow.value1.setTextColor(mContext.resources.getColor(R.color.colorGreenText))
            eventDateRow.value1.text =
                "${jobModel.availableSlots.size} Total Free Slot Found"
            eventDateRow.img1.setImageResource(R.drawable.ic_event_clock)

            eventDateRow.actionLabel.visible()
            eventDateRow.actionLabel.text = "View Slots"
            eventDateRow.actionLabel.setTextColor(mContext.resources.getColor(R.color.colorGreenText))
            eventDateRow.actionLabel.setSafeOnClickListener {
                findNavController().navigate(
                    JobDetailFragmentDirections.actionJobDetailToCalendarFragment(
                        jobModel.userId,
                        binding.check.isVisible,
                        jobModel.userProfile.name.toString(),
                        jobModel.userProfile.profile_image!!,
                        jobModel.userProfile.city!!
                    )
                )
            }
            eventDateRow.actionLabel.setCompoundDrawablesWithIntrinsicBounds(
                null, null, mContext.resources.getDrawable(R.drawable.ic_calendar), null
            )

            eventDateRow.viewEvent.backgroundTintList =
                mContext.resources.getColorStateList(R.color.colorGreenDull)

            eventPhotographyType.root.gone()

            eventCameraRequired.label1.text = getString(R.string.photo_cam_available)
            eventCameraRequired.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventCameraRequired.value1.text = jobModel.photoCameraUse.listToBreakables()
            eventCameraRequired.img1.setImageResource(R.drawable.ic_event_cam)

            eventVideoRequired.label1.text = getString(R.string.video_cam_available)
            eventVideoRequired.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventVideoRequired.value1.text = jobModel.videoCameraUse.listToBreakables()
            eventVideoRequired.img1.setImageResource(R.drawable.ic_event_video)

            eventEquipmentRequired.label1.text = getString(R.string.equipments_available)
            eventEquipmentRequired.value1.setTextColor(mContext.resources.getColor(R.color.colorPurpleText))
            eventEquipmentRequired.value1.text = jobModel.otherEquipments.listToBreakables()
            eventEquipmentRequired.img1.setImageResource(R.drawable.ic_event_equipment)

            eventPhotographerCount.root.gone()

            bookmark.setSafeOnClickListener {
                if (!isBookmarked) {
                    showProgress()
                    jobHomeViewModel.saveFavourite(jobId)
                } else {
                    showProgress()
                    jobHomeViewModel.removeFavourite(favId)
                }
            }

            share.setSafeOnClickListener {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, jobModel.shareLink)
                startActivity(Intent.createChooser(share, ""))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
    }

    private fun initData() {
        binding.scroll.invisible()
        showProgress()
        jobHomeViewModel.jobById(jobId)
    }
}