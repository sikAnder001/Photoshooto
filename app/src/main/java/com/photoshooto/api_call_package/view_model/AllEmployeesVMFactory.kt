package com.photoshooto.api_call_package.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.photoshooto.api_call_package.Repository2

class AllEmployeesVMFactory (private val repository: Repository2) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllEmployeesViewModel(repository) as T
    }
}