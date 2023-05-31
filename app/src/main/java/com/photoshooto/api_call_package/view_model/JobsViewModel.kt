package com.photoshooto.api_call_package.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.domain.model.ApproveRejectBody
import com.photoshooto.domain.model.UpdateProductStatusBody
import com.photoshooto.domain.model.UserJobListingDataRS
import com.photoshooto.domain.model.get_productlist_model.ProductListResponse
import com.photoshooto.domain.model.job_by_id.JobByIdResponse
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.model.save_report_model.SaveSpamReportResponse
import com.photoshooto.domain.model.spam_model.SpamReportResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class JobsViewModel(private val repository: Repository2) : ViewModel() {
    val getJobsResponse = MutableLiveData<Response<JobResponse>>()
    val getJobByIdResponse = MutableLiveData<Response<JobByIdResponse>>()
    val getSaveSpamReportsResponse = MutableLiveData<Response<SaveSpamReportResponse>>()
    val getReportedResponse = MutableLiveData<Response<SpamReportResponse>>()
    val approveDeclineResponse = MutableLiveData<Response<ApproveDeclineResponse>>()
    val removeJobResponse = MutableLiveData<Response<ApproveDeclineResponse>>()
    val usersJobList: MutableLiveData<Response<UserJobListingDataRS>> = MutableLiveData()
    val getProductListResponse = MutableLiveData<Response<ProductListResponse>>()
    val approveDeclineResponse2 = MutableLiveData<Response<ApproveDeclineResponse>>()


    fun getJobs(limit: Int, order: Int?, sort: String) {
        viewModelScope.launch {
            getJobsResponse.value = repository.getJob(limit, order, sort)
        }
    }



    fun saveSpamReports(productId: String,message: String) {
        viewModelScope.launch {
            getSaveSpamReportsResponse.value = repository.saveSpamReports(productId,message)
        }
    }

    fun getJobById(id: String?) {
        viewModelScope.launch {
            getJobByIdResponse.value = repository.getJobById(id)
        }
    }

    fun getReportedSpam(limit: Int, page: Int, order: Int?) {
        viewModelScope.launch {
            getReportedResponse.value = repository.getReportedSpam(limit, page, order)
        }
    }

    fun updateStatus(status: String, products: UpdateProductStatusBody) {
        viewModelScope.launch {
            approveDeclineResponse2.value = repository.updateStatus(status, products)
        }
    }



    fun approveDeclineStatus(status: String, jobs: ApproveRejectBody) {
        viewModelScope.launch {
            approveDeclineResponse.value = repository.approveDeclineStatus(status, jobs)
        }
    }

    fun removeJob(id: String) {
        viewModelScope.launch {
            removeJobResponse.value = repository.removeJob(id)
        }
    }

    fun getJobsListing(
        currentPage: Int,
        prefillFilterJSON: JSONObject?,
        sortByPrefill: String?,
        order: Int?
    ) {
        viewModelScope.launch {
            usersJobList.value =
                repository.getJobsList(currentPage, prefillFilterJSON, sortByPrefill, order)
        }
    }
}