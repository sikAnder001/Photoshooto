package com.photoshooto.ui.job.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentRecyclerBinding
import com.photoshooto.ui.dialog.ConfirmationDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.MyPostListAdapter
import com.photoshooto.ui.job.utility.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPostFragment : BaseFragment() {

    lateinit var binding: FragmentRecyclerBinding
    var currentPage = 0
    var isloading: Boolean = false
    var hasMoreData: Boolean = true
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var myPostListAdapter: MyPostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, x: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPage = 0

        myPostListAdapter = MyPostListAdapter(
            onShareClick = { model, _ ->
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, model.shareLink)
                startActivity(Intent.createChooser(share, ""))
            },
            onEditClick = { model ->
                val args = Bundle().apply {
                    putString("jobType", model.type)
                    putString("action", "update")
                }
                AppConstant.tmpJobModel = model
                findNavController().navigate(R.id.jobCreate, args)
            },
            onButtonClick = { model, _ ->
                ConfirmationDialog.showDialog(requireContext(), (model.title ?: "").ifBlank {
                    model.eventType.toString()
                }) {
                    var tmpList = myPostListAdapter.currentList.toMutableList()
                    tmpList.remove(model)
                    myPostListAdapter.submitList(tmpList)

                    jobHomeViewModel.deleteJobById(model.id)
                }
            }
        )

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )

            addItemDecoration(
                SpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            adapter = myPostListAdapter

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

        loadData()
        attachObserver()
    }

    private fun attachObserver() {

        jobHomeViewModel.myPostedJob.observe(viewLifecycleOwner) {
            hideProgress()
            isloading = false
            if (it.success) {

                if ((it.data?.page == 0) && it.data!!.list.isEmpty()) {
                    binding.emptyLayout.apply {
                        textEmpty.text = getString(R.string.empty_post_desc)
                        textHeadEmpty.text = getString(R.string.empty_post)
                        emptyImg.setImageResource(R.drawable.empty_post)
                        root.visible()
                    }
                } else {
                    binding.emptyLayout.root.gone()
                }

                it.data!!.list.let { jobList ->

                    if (jobList.isNotEmpty()) {
                        if (currentPage == 0) myPostListAdapter.submitList(null)
                        myPostListAdapter.submitList(myPostListAdapter.currentList.plus(jobList))
                    } else {
                        hasMoreData = false
                    }
                }
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

        jobHomeViewModel.deleteJob.observe(viewLifecycleOwner) {
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
        if (currentPage == 0) showProgress()
        var prefill = SavedJobsMyPostFragment.sortByPrefill
        jobHomeViewModel.getMyPostedPost(currentPage, prefill)
    }
}