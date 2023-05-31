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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentPhotographersAvailableBinding
import com.photoshooto.databinding.LayoutToolbarSortJobBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.domain.model.ReviewFeedbackModel
import com.photoshooto.ui.dialog.ReportDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.PhotographerListAdapter
import com.photoshooto.ui.job.utility.*
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PhotographersAvailableFragment : BaseFragment() {

    lateinit var binding: FragmentPhotographersAvailableBinding
    lateinit var toolbarBinding: LayoutToolbarSortJobBinding
    var currentPage = 0
    var globalPositionClick = 0
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var photographListAdapter: PhotographerListAdapter
    private var totalItems: Int = 0
    var sortByPrefill = ""
    var prefillFilterJSON = JSONObject()
    var isloading: Boolean = false
    var hasMoreData: Boolean = true
    var location: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotographersAvailableBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutToolbarSortJobBinding.bind(binding.root)
        toolbarBinding.title.text = getString(R.string.available_photographers_near_you)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPage = 0
        loadData()

        toolbarBinding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        toolbarBinding.icSort.setSafeOnClickListener {
            findNavController().navigate(
                PhotographersAvailableFragmentDirections.actionPhotographerListToDialogSort(
                    sortByPrefill,
                    "Hire Me"
                )
            )
        }

        toolbarBinding.icFilter.setSafeOnClickListener {
            findNavController().navigate(
                PhotographersAvailableFragmentDirections.actionPhotographerListToDialogFilter(
                    "Hire Me",
                    prefillFilterJSON.toString()
                )
            )
        }

        binding.btnCreate.apply {
            text = getString(R.string.create_availability)
            setSafeOnClickListener {
                findNavController().navigate(
                    PhotographersAvailableFragmentDirections.actionPhotographerListToCreateJob(
                        JobCreateFragment.PostType.GET_HIRED.toString()
                    )
                )
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "dialog_sort",
            viewLifecycleOwner
        ) { requestKey, result ->
            sortByPrefill = result.getString("sort_by") ?: ""
            loadData()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "dialog_filter", viewLifecycleOwner
        ) { requestKey, result ->
            prefillFilterJSON = JSONObject(result.getString("filter_json"))
            loadData()
        }


        photographListAdapter = PhotographerListAdapter(onShareClick = {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, it.shareLink)
            startActivity(Intent.createChooser(share, ""))
        }, onBookmarkClick = { model, type, position ->
            globalPositionClick = position
            if (type.equals("remove")) {
                //remove bookmark tag
                jobHomeViewModel.removeFavourite(model.favorite[0].id)
            } else {
                //add bookmark tag
                jobHomeViewModel.saveFavourite(model.id)
            }
        }, onButtonClick = {
            findNavController().navigate(
                PhotographersAvailableFragmentDirections
                    .actionPhotographerListToJobDetail(it.id)
            )
        }, onReportClick = {
            spamReport(it)
        })


        with(binding.recycler) {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )

            addItemDecoration(
                SpacesItemDecorationWithButton(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            adapter = photographListAdapter

            itemAnimator = null

            addOnScrollListener(object :
                EndlessPaginationScrollListener() {
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
        radioList.addAll(resources.getStringArray(R.array.report_user))

        val tmp = "Report User, ${it.userProfile.name}"

        val spannableString = SpannableString(tmp)
        spannableString.setSpan(
            nameTitle, 12, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ReportDialog.showDialog(
            requireContext(), spannableString, radioList
        ) { message ->
            jobHomeViewModel.spamReport(it.id, message, "user")
        }

    }

    private fun attachObserver() {

        jobHomeViewModel.usersAvailabilityLiveData.observe(viewLifecycleOwner) { data ->
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
                        "Total available photographers ", spannableString
                    )



                    totalItems = availablity?.total_count ?: 0

                    if ((availablity?.page == 0) && availablity.list.isEmpty()) {
                        binding.emptyLayout.apply {
                            textEmpty.text = getString(R.string.no_photographer_desc)
                            textHeadEmpty.text = getString(R.string.no_photographers)
                            emptyImg.setImageResource(R.drawable.empty_photographers)
                            root.visible()
                        }
                    } else {
                        binding.emptyLayout.root.gone()
                    }

                    if (availablity?.list?.isNotEmpty() == true) {
                        if (availablity.page == 0) {
                            currentPage = 0
                            photographListAdapter.submitList(null)
                        }
                        photographListAdapter.submitList(
                            photographListAdapter
                                .currentList.plus(availablity.list)
                        )
                    } else {
                        if (availablity?.page == 0) {
                            currentPage = 0
                            photographListAdapter.submitList(null)
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
                photographListAdapter.currentList[globalPositionClick].favorite
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
        jobHomeViewModel.usersAvailabilityList(
            currentPage,
            prefillFilterJSON,
            sortByPrefill,
            location
        )
//        photographListAdapter.notifyDataSetChanged()
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

            val snackbar: Snackbar = Snackbar
                .make(
                    binding.root,
                    getString(R.string.permission_always_denied),
                    Snackbar.LENGTH_INDEFINITE
                )
                .setAction("Settings") {
                    activity?.openPermissionSettings()
                }
            snackbar.show()
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