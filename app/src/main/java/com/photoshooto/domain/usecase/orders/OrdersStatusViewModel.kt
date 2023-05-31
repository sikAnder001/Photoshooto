package com.photoshooto.domain.usecase.orders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.UpdateStatus
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel

class OrdersStatusViewModel constructor(private val ordersStatusUseCase: OrdersStatusUseCase) :
    ViewModel() {

    val commonResponseLiveData = MutableLiveData<CommonResponse>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun updateOrderStatus(order_id: String, status: UpdateStatus) {
        showProgressbar.value = true
        Log.i("orderReq", "${status}")
        ordersStatusUseCase.updateOrderStatus(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString(),
            status,
            order_id,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    commonResponseLiveData.value = result
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
        private val TAG = OrdersStatusViewModel::class.java.name
    }
}
