package com.photoshooto.ui.qrcodesetup.createEvent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.*
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.CreateEventUseCase
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateFolderRequest
import kotlinx.coroutines.cancel


class CreateEventViewModel constructor(private val createEventUseCase: CreateEventUseCase) :
    ViewModel() {

    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    val getFolderListResponse = MutableLiveData<GetFolderResponse>()
    val getStandeeListResponse = MutableLiveData<GetStandeeResponse>()
    val getEventTypeListResponse = MutableLiveData<GetEventTypesResponse>()

    //val getQRCodeListResponse = MutableLiveData<GetQRCodeResponse>()
    val createFolderResponse = MutableLiveData<CommonResponse>()
    val createEventResponse = MutableLiveData<CommonResponse>()

    fun getFolderList(token: String?) {
        createEventUseCase.getFolderList(
            viewModelScope, token,
            object : UseCaseResponse<GetFolderResponse> {
                override fun onSuccess(result: GetFolderResponse) {
                    Log.i(TAG, "result: $result")
                    getFolderListResponse.value = result
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                }
            },
        )
    }

    fun getStandeeList(token: String?) {
        createEventUseCase.getStandeeList(
            viewModelScope, token,
            object : UseCaseResponse<GetStandeeResponse> {
                override fun onSuccess(result: GetStandeeResponse) {
                    Log.i(TAG, "result: $result")
                    getStandeeListResponse.value = result
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                }
            },
        )
    }

    fun getEventTypeList(token: String?) {
        createEventUseCase.getEventTypesList(
            viewModelScope, token,
            object : UseCaseResponse<GetEventTypesResponse> {
                override fun onSuccess(result: GetEventTypesResponse) {
                    Log.i(TAG, "result: $result")
                    getEventTypeListResponse.value = result
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                }
            },
        )
    }

    /*fun getQRCodeList(token: String?) {
        showProgressbar.value = true
        createEventUseCase.getQRCodeList(
            viewModelScope, token,
            object : UseCaseResponse<GetQRCodeResponse> {
                override fun onSuccess(result: GetQRCodeResponse) {
                    Log.i(TAG, "result: $result")
                    getQRCodeListResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            },
        )
    }*/

    fun callCreateFolderApi(token: String?, createFolderRequest: CreateFolderRequest) {
        showProgressbar.value = true
        createEventUseCase.callCreateFolder(
            viewModelScope, token, createFolderRequest,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    createFolderResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            },
        )
    }

    fun callCreateEvent(token: String?, createEventRequest: CreateEventRequest) {
        showProgressbar.value = true
        createEventUseCase.callCreateEvent(
            viewModelScope, token, createEventRequest,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    createEventResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            },
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = CreateEventViewModel::class.java.name
    }

}