package com.photoshooto.domain.usecase.landing_screen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.GetLandingScreenResponse
import com.photoshooto.domain.model.UserDashboardModel
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel

class LandingScreenViewModel constructor(private val landingScreenUseCase: LandingScreenUseCase) :
    ViewModel() {
    companion object {
        private val TAG = LandingScreenViewModel::class.java.name
    }

    val landingScreenResponse = MutableLiveData<GetLandingScreenResponse>()
    val userDashboardResponse = MutableLiveData<UserDashboardModel>()
    val messageData = MutableLiveData<String>()
    val showProgressbar = MutableLiveData<Boolean>()

    fun getLandingScreenData(userType: String) {
        landingScreenUseCase.getLandingScreen(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            userType,
            object : UseCaseResponse<GetLandingScreenResponse> {
                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }

                override fun onSuccess(result: GetLandingScreenResponse) {
                    Log.i(TAG, "result: $result")
                    landingScreenResponse.value = result
                    showProgressbar.value = false
                }
            }
        )
    }

    fun getUserLandingScreenData() {
        landingScreenUseCase.getUserLandingScreen(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<UserDashboardModel> {
                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "" + apiError?.message!!)
                    userDashboardResponse.postValue(
                        UserDashboardModel(
                            message = "",
                            success = false,
                            data = null
                        )
                    )
                    messageData.value = apiError.getErrorMessage()
                    showProgressbar.value = false
                }

                override fun onSuccess(result: UserDashboardModel) {
                    Log.i(TAG, "result: $result")
                    userDashboardResponse.postValue(result)
                    showProgressbar.value = false
                }
            }
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}