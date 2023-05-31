package com.photoshooto.ui.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.ui.notification.model.NotificationModel
import com.photoshooto.util.API
import kotlinx.coroutines.cancel

class GetNotificationViewModel constructor(private val useCase: GetNotificationUseCase) :
    ViewModel() {

    val notiLiveData = MutableLiveData<NotificationModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getNotificationList(limit: Int, page: Int) {
        showProgressbar.value = true
        useCase.getNotificationData(
            viewModelScope,
            API.testToken,
            object : UseCaseResponse<NotificationModel> {
                override fun onSuccess(result: NotificationModel) {
                    Log.i(TAG, "result: $result")
                    notiLiveData.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("mazroid apiError " + apiError)
                }
            }
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = GetNotificationViewModel::class.java.name
    }
}
