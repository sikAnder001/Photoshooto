package com.photoshooto.data.di


import com.photoshooto.ui.job.JobViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { JobViewModel(get()) }
}