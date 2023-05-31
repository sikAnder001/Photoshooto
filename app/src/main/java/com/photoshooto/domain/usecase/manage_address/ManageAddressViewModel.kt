package com.photoshooto.domain.usecase.manage_address

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.GetAddressResponse
import com.photoshooto.domain.model.GetStatesResponse
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel

class ManageAddressViewModel constructor(private val manageAddressUseCase: ManageAddressUseCase) :
    ViewModel() {

    val addAddressResponse = MutableLiveData<CommonResponse>()
    val deleteAddressResponse = MutableLiveData<CommonResponse>()
    val getAddressResponse = MutableLiveData<GetAddressResponse>()
    val getStatesResponse = MutableLiveData<GetStatesResponse>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getStates() {
        showProgressbar.value = true
        manageAddressUseCase.getStates(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetStatesResponse> {
                override fun onSuccess(result: GetStatesResponse) {
                    Log.i(TAG, "result: $result")
                    getStatesResponse.value = result
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

    fun deleteAddress(id: String) {

        showProgressbar.value = true
        manageAddressUseCase.deleteAddress(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            id,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    deleteAddressResponse.value = result
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

    fun getAddress() {
        showProgressbar.value = true
        manageAddressUseCase.getAddress(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetAddressResponse> {
                override fun onSuccess(result: GetAddressResponse) {
                    Log.i(TAG, "result: $result")
                    getAddressResponse.value = result
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

    fun addAddress(
        address: String,
        city: String,
        flatNo: String,
        isDefault: Boolean,
        landMark: String,
        lat: Long,
        lng: Long,
        pinCode: String,
        state: String,
        type: String
    ) {
        val request = AddAddressRequest(
            address, city, flatNo, isDefault, landMark, lat, lng, pinCode, state, type
        )
        showProgressbar.value = true
        manageAddressUseCase.addAddress(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            request,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    addAddressResponse.value = result
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

    fun updateAddress(
        id: String,
        address: String,
        city: String,
        flatNo: String,
        isDefault: Boolean,
        landMark: String,
        lat: Long,
        lng: Long,
        pinCode: String,
        state: String,
        type: String
    ) {
        val request = AddAddressRequest(
            address, city, flatNo, isDefault, landMark, lat, lng, pinCode, state, type
        )
        showProgressbar.value = true
        manageAddressUseCase.updateAddress(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            id,
            request,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    addAddressResponse.value = result
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
        private val TAG = ManageAddressViewModel::class.java.name
    }
}
