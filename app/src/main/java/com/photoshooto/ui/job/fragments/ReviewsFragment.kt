package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentReviewBinding
import com.photoshooto.domain.model.ReviewFeedbackModel
import com.photoshooto.ui.dialog.ReviewDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.ReviewListAdapter
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewsFragment : BaseFragment() {

    lateinit var binding: FragmentReviewBinding
    private lateinit var reviewListAdapter: ReviewListAdapter
    var reviewList = arrayListOf<ReviewFeedbackModel>()
    var totalRating = 0
    private var isSelfView = false
    private val navArgs: ReviewsFragmentArgs by navArgs()

    private val jobHomeViewModel: JobViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        val toolbarBinding = binding.toolbarReviewFragment
        toolbarBinding.tvTitle.text = "${navArgs.userName} Ratings"
        toolbarBinding.backBtn.setSafeOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSelfView = navArgs.userId.equals(SharedPrefsHelper.read(SharedPrefConstant.USER_ID), true)

        if (isSelfView) {
            binding.btnWriteReview.invisible()
        } else {
            binding.btnWriteReview.visible()
        }

        binding.profile.userName.text = navArgs.userName
        binding.profile.userId.text = "ID: ${navArgs.userId}"
        binding.profile.city.text = navArgs.userAddress
        binding.profile.check.isVisible = navArgs.userSubscribe

        loadImageUser(binding.profile.imageViewProfile, navArgs.userProfile)

        reviewListAdapter = ReviewListAdapter {

        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = reviewListAdapter

            itemAnimator = null

            addItemDecoration(
                SpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
        }

        binding.btnWriteReview.setSafeOnClickListener {
            ReviewDialog.showDialog(requireContext(), navArgs.userName) { feedback, rating ->
                showProgress()
                jobHomeViewModel.submitFeedback(rating, feedback, navArgs.userId)
            }
        }

        attachObserver()
    }

    private fun attachObserver() {
        jobHomeViewModel.feedbackUserById.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                it.data.apply {

                    reviewList.clear()

                    reviewList.addAll(this.userReviews)

                    var max =
                        ratings.star5 + ratings.star4 + ratings.star3 +
                                ratings.star2 + ratings.star1

                    binding.totalRating.text = overalRating.singleDecimalPlaces()

                    binding.star5.apply {
                        starLabel.text = "5 Stars"
                        starValue.text = "${ratings.star5}%"
                    }
                    binding.star4.apply {
                        starLabel.text = "4 Stars"
                        starValue.text = "${ratings.star4}%"
                    }
                    binding.star3.apply {
                        starLabel.text = "3 Stars"
                        starValue.text = "${ratings.star3}%"
                    }
                    binding.star2.apply {
                        starLabel.text = "2 Stars"
                        starValue.text = "${ratings.star2}%"
                    }
                    binding.star1.apply {
                        starLabel.text = "1 Star"
                        starValue.text = "${ratings.star1}%"
                    }

                    if (max == 0) {
                        max = 1
                    }

                    binding.star5.starValue.text = "${((ratings.star5 * 100) / max).toInt()}%"
                    binding.star4.starValue.text = "${((ratings.star4 * 100) / max).toInt()}%"
                    binding.star3.starValue.text = "${((ratings.star3 * 100) / max).toInt()}%"
                    binding.star2.starValue.text = "${((ratings.star2 * 100) / max).toInt()}%"
                    binding.star1.starValue.text = "${((ratings.star1 * 100) / max).toInt()}%"

                    binding.star5.progressBar.max = max
                    binding.star4.progressBar.max = max
                    binding.star3.progressBar.max = max
                    binding.star2.progressBar.max = max
                    binding.star1.progressBar.max = max

                    binding.star5.progressBar.progress = ratings.star5
                    binding.star4.progressBar.progress = ratings.star4
                    binding.star3.progressBar.progress = ratings.star3
                    binding.star2.progressBar.progress = ratings.star2
                    binding.star1.progressBar.progress = ratings.star1

                    reviewListAdapter.submitList(reviewList)
                }
            } else {
                requireContext().toastError(it.message.toString())
            }
        }

        jobHomeViewModel.submitFeedback.observe(viewLifecycleOwner) {
            initData()
            if (it.success) {
                requireContext().toastSuccess(it.message.toString())
            } else {
                requireContext().toastError(it.message.toString())
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
        showProgress()
        jobHomeViewModel.getFeedbackById(navArgs.userId)
    }
}