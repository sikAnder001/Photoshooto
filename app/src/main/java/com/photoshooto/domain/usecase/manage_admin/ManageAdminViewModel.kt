package com.photoshooto.domain.usecase.manage_admin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.photoshooto.domain.model.*
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel

class ManageAdminViewModel constructor(private val manageUsersUseCase: ManageUsersUseCase) :
    ViewModel() {

    val ACTIVE = "active"
    val INACTIVE = "inactive"
    val REJECT = "reject"
    val ACCEPT = "accept"
    val BLOCK = "block"
    val REMOVE = "remove"

    val superAdminResponse = MutableLiveData<GetUsersResponse>()
    val adminResponse = MutableLiveData<GetUsersResponse>()
    val jobResponse = MutableLiveData<JobResponse>()

    val createUserResponse = MutableLiveData<CommonResponse>()
    val removeUserResponse = MutableLiveData<CommonResponse>()
    val updateUserStatusResponse = MutableLiveData<CommonResponse>()
    val getModulesResponse = MutableLiveData<GetModulesResponse>()
    val getCityResponse = MutableLiveData<GetCityResponse>()

    val getUserDetailsResponse = MutableLiveData<GetUserDetailsResponse>()

    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()


    fun getJobs() {
        showProgressbar.value = true
        manageUsersUseCase.getJobs(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<JobResponse> {
                override fun onSuccess(result: JobResponse) {
                    Log.i("AllData", "${result.data.toString()}")
                    Log.i(TAG, "result: $result")
                    jobResponse.value = result
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


    fun getAdmin() {
        showProgressbar.value = true
        manageUsersUseCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            0,
            "admin",
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    adminResponse.value = result
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

    fun getModulesList() {
        showProgressbar.value = true
        manageUsersUseCase.getModulesList(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetModulesResponse> {
                override fun onSuccess(result: GetModulesResponse) {
                    Log.i(TAG, "Modules  : result: $result")
                    getModulesResponse.value = result
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

    fun getCityList() {
        showProgressbar.value = true
        manageUsersUseCase.getCityList(
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

    fun getSuperAdmin() {
        showProgressbar.value = true
        manageUsersUseCase.getUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            10,
            0,
            "superadmin",
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    superAdminResponse.value = result
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
        manageUsersUseCase.getUserDetails(
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

    fun createUser(
        email: String,
        altEmail: String,
        empCode: String,
        gender: String,
        mobile: String,
        name: String,
        password: String,
        type: String,
        languagesKnown: List<String>? = null,
        assignedCities: List<String>,
        assignedModules: List<String>
    ) {
        val profileDetails =
            ProfileDetails(
                email = email,
                gender = gender,
                mobile = mobile,
                name = name,
                alt_email = altEmail,
                language_know = languagesKnown,
                city_assigned = assignedCities,
                module_assigned = assignedModules
            )
        // type = "Photographer",
        val request =
            AddUserRequest(
                password = password,
                employee_by = type,
                role = "admin",
                employee_code = empCode,
                profile_details = profileDetails
            )
        Log.i("createAdminReq", Gson().toJson(request))
        manageUsersUseCase.createUser(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            request,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    createUserResponse.value = result
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


    fun removeUser(
        id: String
    ) {
        manageUsersUseCase.removeUser(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            id,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    removeUserResponse.value = result
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

    fun updateUserStatus(id: String, status: String) {
        val request = UpdateUserStatusRequest(status = status)
        showProgressbar.value = true
        manageUsersUseCase.updateUserStatus(
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

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }


    companion object {
        private val TAG = ManageAdminViewModel::class.java.name
    }
}
