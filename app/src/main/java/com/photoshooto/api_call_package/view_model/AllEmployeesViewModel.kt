package com.photoshooto.api_call_package.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.domain.model.all_employees_model.AllEmployeesResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class AllEmployeesViewModel(private val repository: Repository2) : ViewModel()  {
    val getAllEmployeesResponse = MutableLiveData<Response<AllEmployeesResponse>>()

    fun getAllUsers(limit: Int, page: Int,sort: String,order: Int?,type: String) {
        viewModelScope.launch {
            getAllEmployeesResponse.value = repository.getAllUsers(limit, page,sort,order,type)
        }
    }
}