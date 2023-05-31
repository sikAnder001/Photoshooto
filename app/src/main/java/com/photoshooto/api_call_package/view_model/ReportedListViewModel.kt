package com.photoshooto.api_call_package.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.domain.model.reportedListModel.ReportedListResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class ReportedListViewModel (private val repository: Repository2) : ViewModel() {
    val getReportedListResponse = MutableLiveData<Response<ReportedListResponse>>()
    val removeListResponse = MutableLiveData<Response<ApproveDeclineResponse>>()


    fun getReportedList(limit: Int, page: Int,Spam_type:String,order: Int?,sort: String) {
        viewModelScope.launch {
            getReportedListResponse.value = repository.getReportedList(limit, page,Spam_type,order,sort)
        }
    }

    fun removeList(id: String) {
        viewModelScope.launch {
            removeListResponse.value = repository.removeList(id)
        }
    }


}