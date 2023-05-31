package com.photoshooto.ui.job.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentRecyclerBinding
import com.photoshooto.domain.model.BookmarkJob
import com.photoshooto.ui.dialog.ReportDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.BookmarkedListAdapter
import com.photoshooto.ui.job.utility.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SavedFragment : BaseFragment() {

    lateinit var binding: FragmentRecyclerBinding
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var bookmarkedListAdapter: BookmarkedListAdapter
    var currentPage = 0
    var isloading: Boolean = false
    var hasMoreData: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPage = 0

        bookmarkedListAdapter = BookmarkedListAdapter(
            onShareClick = { model, type ->
                val tmpLink = "https://photoshooto.com/jobs/" + model.jobId
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, tmpLink)
                startActivity(Intent.createChooser(share, ""))
            },
            onBookmarkClick = { model ->
                jobHomeViewModel.removeFavourite(model.id)
            },
            onButtonClick = { model, type ->
                val args = Bundle().apply {
                    putString("jobId", model.jobId)
                }
                findNavController().navigate(R.id.jobDetail, args)
            },
            onReportClick = { model, type ->
                val nameTitle = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(requireContext(), R.color.colorOrange)
                        ds.isUnderlineText = false
                    }
                }

                val radioList = arrayListOf<String>()
                var tmp = ""
                var userType = ""
                if (type == "HIRE_YOU") {
                    userType = "job"
                    radioList.addAll(resources.getStringArray(R.array.report_job))
                    tmp = "Report Job, ${model.jobId}"
                } else {
                    userType = "user"
                    radioList.addAll(resources.getStringArray(R.array.report_user))
                    tmp = "Report User, ${model.userId}"
                }

                val spannableString = SpannableString(tmp)
                spannableString.setSpan(
                    nameTitle, 12, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                ReportDialog.showDialog(
                    requireContext(), spannableString, radioList
                ) { message ->
                    if (!model.jobId.isNullOrBlank()) {
                        jobHomeViewModel.spamReport(model.jobId, message, userType)
                    }
                }
            },
        )

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )

            itemAnimator = null

            addItemDecoration(
                SpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            adapter = bookmarkedListAdapter
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
        attachObserver()
    }

    private fun attachObserver() {
        jobHomeViewModel.bookmarkPost.observe(viewLifecycleOwner) {
            hideProgress()
            isloading = false
            if (it.success) {
                it.data?.list?.let { savedList ->
                    var tmpList = mutableListOf<BookmarkJob>()
                    savedList.forEach {
                        if (it.jobDetails.description != null)
                            tmpList.add(it)
                    }

                    if ((it.data.page == 0) && savedList.isEmpty()) {
                        binding.emptyLayout.apply {
                            textEmpty.text = getString(R.string.saved_post_desc)
                            textHeadEmpty.text = getString(R.string.saved_post)
                            emptyImg.setImageResource(R.drawable.empty_bookmark)
                            root.visible()
                        }
                    } else {
                        binding.emptyLayout.root.gone()
                    }

                    if (savedList.isNotEmpty()) {
                        if (currentPage == 0) bookmarkedListAdapter.submitList(null)
                        bookmarkedListAdapter.submitList(
                            bookmarkedListAdapter.currentList.plus(tmpList)
                        )
                    } else {
                        hasMoreData = false
                    }
                }

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

        jobHomeViewModel.spamReport.observe(viewLifecycleOwner) {
            if (it.success) {
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
            }
        }
    }

    fun loadData(fromMain: Boolean = false) {
        if (fromMain) currentPage = 0
        isloading = true
        var prefill = SavedJobsMyPostFragment.sortByPrefill
        if (currentPage == 0) showProgress()
        jobHomeViewModel.getBookmarkPost(currentPage, prefill)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}