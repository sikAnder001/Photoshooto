package com.photoshooto.domain.usecase.payment_success

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.PaymentRequestModel
import com.photoshooto.util.Resource
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PaymentSuccessViewModel constructor(private val useCase: PaymentSuccessUseCase) :
    ViewModel() {

    private val _paymentSuccessEvent = MutableLiveData<Resource<AllStatusModel>>()
    val paymentSuccessEvent: LiveData<Resource<AllStatusModel>> get() = _paymentSuccessEvent

    val paymentRequest = PaymentRequestModel()

    val showProgressbar = MutableLiveData<Boolean>()

    fun initiatePaymentSuccess() {
        //partMap: PaymentSuccessRequest
        val token = SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString()
        showProgressbar.value = true
        _paymentSuccessEvent.postValue(Resource.loading())
        Log.i("paymentReq&Status", "Result : ${paymentRequest}")
        viewModelScope.launch {
            useCase.paymentSuccess(token, paymentRequest).let {
                if (it.isSuccessful) {
                    try {
                        Log.i("paymentReq&Status", "Result : ${it.body()}")
                    } catch (e: Exception) {
                    }
                    _paymentSuccessEvent.postValue(Resource.success(it.body()))
                } else {
                    _paymentSuccessEvent.postValue(Resource.error("Something went wrong", null))
                }
            }
        }
    }


    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

}