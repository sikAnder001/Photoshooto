package com.photoshooto.domain.usecase.manage_request

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.R
import com.photoshooto.domain.model.*
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel

class NewRequestViewModel constructor(private val manageRequestUseCase: ManageRequestUseCase) :
    ViewModel() {

    val SEARCH_DELAY: Long = 700L
    val ACTIVE = "active"
    val INACTIVE = "inactive"
    val REJECT = "reject"
    val ACCEPT = "accept"
    val BLOCK = "block"

    object SORT_BY {
        val new_to_old = "createdAt"
        val old_to_new = "createdAt"
        val last_1_week = ""
        val last_1_month = ""
        val alphabetically = "profile_details.name"
        val order_acd = "1"
        val order_desc = "-1"
    }

    val newRequestResponse = MutableLiveData<GetUsersResponse>()
    val acceptedRequestResponse = MutableLiveData<GetUsersResponse>()
    val declineRequestResponse = MutableLiveData<GetUsersResponse>()
    val blockedRequestResponse = MutableLiveData<GetUsersResponse>()


    val getUserDetailsResponse = MutableLiveData<GetUserDetailsResponse>()
    val getReasonsResponse = MutableLiveData<GetReasonsResponse>()

    val updateUserStatusResponse = MutableLiveData<CommonResponse>()

    val getCityResponse = MutableLiveData<GetCityResponse>()

    val newRequestDetails = MutableLiveData<NewRequestModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getReasons() {
        showProgressbar.value = true
        manageRequestUseCase.getReasons(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetReasonsResponse> {
                override fun onSuccess(result: GetReasonsResponse) {
                    Log.i(TAG, "result: $result")
                    getReasonsResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun getRequestUser(
        page: Int,
        query: String?,
        sortBy: String?,
        order: String?,
        filter: String?
    ) {
        showProgressbar.value = true
        manageRequestUseCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            page,
            query,
            sortBy,
            order,
            filter,
            ACTIVE,
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    newRequestResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun getAcceptedRequestUser(
        page: Int,
        query: String?,
        sortBy: String?,
        order: String?,
        filter: String?
    ) {
        showProgressbar.value = true
        manageRequestUseCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            page,
            query,
            sortBy,
            order,
            filter,
            ACCEPT,
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    acceptedRequestResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun getRejectedRequestUser(
        page: Int,
        query: String?,
        sortBy: String?,
        order: String?,
        filter: String?
    ) {
        showProgressbar.value = true
        manageRequestUseCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            page,
            query,
            sortBy,
            order,
            filter,
            REJECT,
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    declineRequestResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun getBlockedRequestUser(
        page: Int,
        query: String?,
        sortBy: String?,
        order: String?,
        filter: String?
    ) {
        showProgressbar.value = true
        manageRequestUseCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            page,
            query,
            sortBy,
            order,
            filter,
            BLOCK,
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    blockedRequestResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun getUserDetails(id: String) {
        showProgressbar.value = true
        manageRequestUseCase.getUserDetails(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            id,
            object : UseCaseResponse<GetUserDetailsResponse> {
                override fun onSuccess(result: GetUserDetailsResponse) {
                    Log.i(TAG, "result: $result")
                    getUserDetailsResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun updateUserStatus(id: String, status: String, reasons: List<String>? = null) {
        val request = UpdateUserStatusRequest(status = status, reason = reasons)
        showProgressbar.value = true
        manageRequestUseCase.updateUserStatus(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            id,
            request,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    updateUserStatusResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun initTestData() {
        val requestList = ArrayList<RequestedUser>()
        (0..10).forEach {
            requestList.add(
                RequestedUser(
                    profileImage = R.drawable.ic_temp_user_profile,
                    id = "PS22BGLR9876",
                    name = "Rishabh Sewani",
                    designation = "Wedding Photographer",
                    location = "Rajaji Nagar, Bengaluru",
                    requestTime = "7H 24Min",
                    date1 = "26/08/2022 07:30pm",
                    date2 = "26/08/2022 07:30pm"
                )
            )
        }

        val filterList = ArrayList<RequestedFilterItem>()
        filterList.add(RequestedFilterItem("Mumbai"))
        filterList.add(RequestedFilterItem("Chennai"))
        filterList.add(RequestedFilterItem("Banglore"))
        filterList.add(RequestedFilterItem("Kolkata"))
        filterList.add(RequestedFilterItem("Hyderabad"))
        filterList.add(RequestedFilterItem("Pune"))
        val data = NewRequestData(requestList = requestList, filterList = filterList)
        val model = NewRequestModel(success = true, message = "", data = data)
        newRequestDetails.value = model
    }

    fun getCityList() {
        showProgressbar.value = true
        manageRequestUseCase.getCityList(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetCityResponse> {
                override fun onSuccess(result: GetCityResponse) {
                    Log.i(TAG, "result: $result")
                    getCityResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = NewRequestViewModel::class.java.name
    }
}
