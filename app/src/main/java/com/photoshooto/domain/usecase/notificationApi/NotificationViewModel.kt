package com.photoshooto.domain.usecase.notificationApi

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.cancel

class NotificationViewModel constructor(private val notificationRequestUseCase: NotificationRequestUseCase) :
    ViewModel() {

    val reqNotifyModel = MutableLiveData<AllStatusModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun reqNotifications(notifyModel: NotificationRequestModel, token: String?) {
        showProgressbar.value = true
        notificationRequestUseCase.reqNotificationsCall(
            viewModelScope, notifyModel, token,
            object : UseCaseResponse<AllStatusModel> {
                override fun onSuccess(result: AllStatusModel) {
                    Log.i(TAG, "result: $result")
                    reqNotifyModel.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("praveen apiError " + apiError)
                }
            },
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = NotificationViewModel::class.java.name
    }

}