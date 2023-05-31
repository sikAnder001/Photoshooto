package com.photoshooto.domain.usecase.manage_users

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.GetUserDetailsResponse
import com.photoshooto.domain.model.GetUsersResponse
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel

class ManageUsersViewModel constructor(private val manageUsersCase: ManageUsersCase) :
    ViewModel() {

    val userListResponse = MutableLiveData<GetUsersResponse>()
    val getUserDetailsResponse = MutableLiveData<GetUserDetailsResponse>()

    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getRequestUser() {
        showProgressbar.value = true
        manageUsersCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            0,
            "admin",
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    userListResponse.value = result
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
        manageUsersCase.getUserDetails(
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

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = ManageUsersViewModel::class.java.name
    }
}
