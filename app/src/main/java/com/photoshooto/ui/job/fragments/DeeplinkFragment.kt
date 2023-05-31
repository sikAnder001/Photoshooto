package com.photoshooto.ui.job.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentDeeplinkBinding
import com.photoshooto.ui.dialog.ErrorDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.clearApplicationData
import com.photoshooto.util.clearPreferences
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeeplinkFragment : BaseFragment() {

    lateinit var binding: FragmentDeeplinkBinding
    private val navArgs: DeeplinkFragmentArgs by navArgs()

    private val jobHomeViewModel: JobViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDeeplinkBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobHomeViewModel.jobById.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {

                if (it.list.isEmpty()) {
                    ErrorDialog.showDialog(
                        requireContext(),
                        getString(R.string.invalid_job),
                        getString(R.string.okay),
                        false
                    ) {
                        findNavController().navigateUp()
                    }
                } else {
                    it.list.first {
                        findNavController().navigate(
                            DeeplinkFragmentDirections.actionDeeplinkFragmentToJobDetail(navArgs.jobId)
                        )
                        true
                    }
                }
            }
        }
    }

    private fun moveToLogin() {
        logoutAndRedirect()
    }

    private fun logoutAndRedirect() {
        SharedPrefsHelper.clearAllPreferences()
        SharedPrefsHelper.write(SharedPrefConstant.SHOW_ON_BOARDING, true)
        clearApplicationData(requireContext())
        clearPreferences(requireContext())
        navigateToAuthScreen()
    }

    private fun navigateToAuthScreen() {
        val intent = Intent(activity, AuthActivity::class.java)
        intent.apply {
            startActivity(this)
            activity?.finish()
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
        Log.w("navArgssss", "##  ${navArgs.jobId}")

        if (SharedPrefsHelper.isUserLoggedIn())
            jobHomeViewModel.jobById(navArgs.jobId)
        else
            moveToLogin()
    }
}