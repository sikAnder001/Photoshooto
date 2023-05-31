package com.photoshooto.api_call_package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.photoshooto.api_call_package.view_model.ReportedListViewModel

class ReportedListVMFactory(private val repository: Repository2) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportedListViewModel(repository) as T
    }
}