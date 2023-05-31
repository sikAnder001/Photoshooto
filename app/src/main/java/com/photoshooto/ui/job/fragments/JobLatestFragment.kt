package com.photoshooto.ui.job.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.VMFactory
import com.photoshooto.api_call_package.view_model.JobsViewModel
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentPhotographersAvailableBinding
import com.photoshooto.databinding.LayoutToolbarSortJobBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.domain.model.ReviewFeedbackModel
import com.photoshooto.ui.dialog.ReportDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.JobListAdapter
import com.photoshooto.ui.job.utility.*
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel


class JobLatestFragment : BaseFragment() {

    lateinit var binding: FragmentPhotographersAvailableBinding
    lateinit var toolbarBinding: LayoutToolbarSortJobBinding
    var sortByPrefill = ""
    var prefillFilterJSON = JSONObject()
    var currentPage = 0
    var globalPositionClick = 0
    var isloading: Boolean = false
    var hasMoreData: Boolean = true
    var location: String? = null

    private lateinit var repository: Repository2
    private lateinit var viewModel1: JobsViewModel
    private lateinit var factory: VMFactory

    //  lateinit var paginate: Paginate
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var jobListAdapter: JobListAdapter
    private var totalItems: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotographersAvailableBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutToolbarSortJobBinding.bind(binding.root)
        toolbarBinding.title.text = getString(R.string.latest_jobs)

        repository = Repository2()

        factory = VMFactory(repository)

        viewModel1 = ViewModelProvider(this, factory)[JobsViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarBinding.back.setOnClickListener {
            activity?.onBackPressed()
        }

        toolbarBinding.icSort.setSafeOnClickListener {
            findNavController().navigate(
                JobLatestFragmentDirections.actionJobListToDialogSort(
                    sortByPrefill,
                    "Hire You"
                )
            )
        }

        toolbarBinding.icFilter.setSafeOnClickListener {
            findNavController().navigate(
                JobLatestFragmentDirections.actionJobListToDialogFilter(
                    "Hire You", prefillFilterJSON.toString()
                )
            )
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "dialog_sort", viewLifecycleOwner
        ) { requestKey, result ->
            Log.v("sortData", result.getString("sort_by") ?: "")
            sortByPrefill = result.getString("sort_by") ?: ""
            loadData()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "dialog_filter", viewLifecycleOwner
        ) { requestKey, result ->

            prefillFilterJSON = JSONObject(result.getString("filter_json"))
            loadData()
        }

        currentPage = 0
        binding.label.text = "Total available jobs"

        binding.btnCreate.apply {
            text = getString(R.string.create_job)
            setSafeOnClickListener {
                findNavController().navigate(
                    JobLatestFragmentDirections.actionJobListToCreateJob(
                        JobCreateFragment.PostType.POST_JOB.toString()
                    )
                )
            }
        }

        jobListAdapter = JobListAdapter(onShareClick = {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage = ""
                shareMessage =
                    """${shareMessage + "www.photoshooto.com/JobListing/${it.id}"}
                            """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                context!!.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {
                //e.toString();
            }
        }, onBookmarkClick = { model, type, position ->
            globalPositionClick = position
            if (type == "remove") {
                //remove bookmark tag
                jobHomeViewModel.removeFavourite(model.favorite[0].id)
            } else {
                //add bookmark tag
                jobHomeViewModel.saveFavourite(model.id)
            }
        }, onButtonClick = {
            findNavController().navigate(
                JobLatestFragmentDirections.actionJobListToJobDetail(it.id)
            )
        }, onReportClick = {
            spamReport(it)
        })

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )

            addItemDecoration(
                SpacesItemDecorationWithButton(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            adapter = jobListAdapter
            itemAnimator = null

            addOnScrollListener(object : EndlessPaginationScrollListener() {
                override fun requestNewPage() {
                    super.requestNewPage()
                    if (!isloading && hasMoreData) {
                        currentPage++
                        showProgress()
                        loadData()
                    }
                }
            })
        }

        binding.searchLayout.location.setSafeOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )

            MayI.withActivity(requireActivity()).withPermissions(*permissions)
                .onRationale(this::permissionRationaleMultiLocation)
                .onResult(this::permissionResultMultiLocation)
                .onErrorListener(this::inCaseOfErrorLocation).check()
        }

        attachObserver()
        loadData()

        editTextSubmit()
    }

    private fun editTextSubmit() {
        binding.searchLayout.search.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                location = binding.searchLayout.search.text.toString()
                if (!location.isNullOrEmpty()) {
                    loadData()
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun spamReport(it: JobModel) {

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

        val tmp = "Report Job, ${(it.title ?: "").ifBlank { it.eventType }}"

        val spannableString = SpannableString(tmp)
        spannableString.setSpan(
            nameTitle, 12, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ReportDialog.showDialog(
            requireContext(), spannableString, radioList
        ) { message ->
            jobHomeViewModel.spamReport(it.id, message, "job")
        }
    }

    private fun attachObserver() {

//        viewModel1.usersJobList.observe(viewLifecycleOwner) {
//            if (it.isSuccessful && it.code() == 200) {
//                val data= it.body()!!
//                    data.data.let { availablity ->
//
//                        val span = object : ClickableSpan() {
//                            override fun onClick(widget: View) {
//                            }
//
//                            override fun updateDrawState(ds: TextPaint) {
//                                ds.color = ContextCompat.getColor(requireContext(), R.color.textColor)
//                                ds.typeface = Typeface.DEFAULT_BOLD
//                            }
//                        }
//
//                        val spannableString = SpannableString(availablity?.total_count.toString())
//                        spannableString.setSpan(
//                            span,
//                            0,
//                            (availablity?.total_count.toString()).length,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                        )
//
//                        binding.label.text = TextUtils.concat(
//                            "Total available jobs ", spannableString
//                        )
//
//                        totalItems = availablity?.total_count ?: 0
//
//                        if ((availablity?.page == 0) && availablity.list.isEmpty()) {
//                            binding.emptyLayout.apply {
//                                textEmpty.text = getString(R.string.no_job_desc)
//                                textHeadEmpty.text = getString(R.string.no_jobs_available)
//                                emptyImg.setImageResource(R.drawable.empty_job)
//                                root.visible()
//                            }
//                        } else {
//                            binding.emptyLayout.root.gone()
//                        }
//
//                        if (availablity?.list?.isNotEmpty() == true) {
//                            if (availablity.page == 0) {
//                                currentPage = 0
//                                jobListAdapter.submitList(null)
//                            }
//                            jobListAdapter.submitList(jobListAdapter.currentList.plus(availablity.list))
//                        } else {
//                            if (availablity?.page == 0) {
//                                currentPage = 0
//                                jobListAdapter.submitList(null)
//                            }
//                            hasMoreData = false
//                        }
//                    }
//            } else {
//                Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
//            }
//        }


        jobHomeViewModel.usersJobList.observe(viewLifecycleOwner) { data ->
            hideProgress()
            isloading = false
            if (data.success) {
                data.data.let { availablity ->

                    val span = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = ContextCompat.getColor(requireContext(), R.color.textColor)
                            ds.typeface = Typeface.DEFAULT_BOLD
                        }
                    }

                    val spannableString = SpannableString(availablity?.total_count.toString())
                    spannableString.setSpan(
                        span,
                        0,
                        (availablity?.total_count.toString()).length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    binding.label.text = TextUtils.concat(
                        "Total available jobs ", spannableString
                    )

                    totalItems = availablity?.total_count ?: 0

                    if ((availablity?.page == 0) && availablity.list.isEmpty()) {
                        binding.emptyLayout.apply {
                            textEmpty.text = getString(R.string.no_job_desc)
                            textHeadEmpty.text = getString(R.string.no_jobs_available)
                            emptyImg.setImageResource(R.drawable.empty_job)
                            root.visible()
                        }
                    } else {
                        binding.emptyLayout.root.gone()
                    }

                    if (availablity?.list?.isNotEmpty() == true) {
                        if (availablity.page == 0) {
                            currentPage = 0
                            jobListAdapter.submitList(null)
                        }
                        jobListAdapter.submitList(jobListAdapter.currentList.plus(availablity.list))
                        jobListAdapter.notifyDataSetChanged()
                    } else {
                        if (availablity?.page == 0) {
                            currentPage = 0
                            jobListAdapter.submitList(null)
                        }
                        hasMoreData = false
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }

        jobHomeViewModel.spamReport.observe(viewLifecycleOwner) {
            if (it.success) {
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }

        jobHomeViewModel.removeFavourite.observe(viewLifecycleOwner) {
            if (it.success) {
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }

        jobHomeViewModel.saveFavourite.observe(viewLifecycleOwner) {
            if (it.success) {
                val rr = mutableListOf<ReviewFeedbackModel>().apply {
                    add(it.data)
                }
                jobListAdapter.currentList[globalPositionClick].favorite = rr
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }
    }

    private fun loadData() {
        if (binding.searchLayout.search.text.isEmpty())
            location = null

        isloading = true
        if (currentPage == 0) showProgress()
        jobHomeViewModel.userJobListing(0, prefillFilterJSON, sortByPrefill, location)
        jobListAdapter.notifyDataSetChanged()
//        viewModel1.getJobsListing(currentPage, prefillFilterJSON, sortByPrefill,null)
    }

    private fun permissionRationaleMultiLocation(
        permissions: Array<PermissionBean>, token: PermissionToken
    ) {
        token.continuePermissionRequest()
    }

    private fun inCaseOfErrorLocation(e: Exception) {
        requireContext().toastError("Error for permission : " + e.message)
    }

    private fun permissionResultMultiLocation(
        permissions: Array<PermissionBean>
    ) {
        val isAllPermanentlyDenied = permissions.all {
            it.isPermanentlyDenied
        }

        if (isAllPermanentlyDenied) {
            val snackBar: Snackbar = Snackbar.make(
                binding.root,
                getString(R.string.permission_always_denied),
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Settings") {
                activity?.openPermissionSettings()
            }
            snackBar.show()
            return
        }

        val isAllPermissionGranted = permissions.all {
            it.isGranted
        }

        if (isAllPermissionGranted) {
            GetCurrentLocation.getLastLocation(requireContext()) { _, _, address ->
                try {
                    binding.searchLayout.search.setText("")
                    var googleAddress = ""
                    if (address.isNotEmpty()) {
                        googleAddress = (address[0].locality)
                    }
                    binding.searchLayout.search.setText(googleAddress)
                    location = googleAddress
                    loadData()
                    binding.searchLayout.search.setSelection(
                        binding.searchLayout.search.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}