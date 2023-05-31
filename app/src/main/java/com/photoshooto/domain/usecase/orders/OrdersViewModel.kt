package com.photoshooto.domain.usecase.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.InvoiceModel
import com.photoshooto.domain.model.OrderDetailModel
import com.photoshooto.domain.model.OrderListModel
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.util.Resource
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersViewModel constructor(
    private val ordersUseCase: OrdersUseCase,
    private val orderDetailUseCase: OrderDetailUseCase
) :
    ViewModel() {

    private val _invoiceLiveData = MutableLiveData<Resource<InvoiceModel>>()
    val invoiceLiveData: LiveData<Resource<InvoiceModel>> get() = _invoiceLiveData

    val ordersLiveData = MutableLiveData<OrderListModel>()
    val orderDetailLiveData = MutableLiveData<OrderDetailModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getOrderRequestList(type: String, limit: Int, page: Int) {
        showProgressbar.value = true
        ordersUseCase.getOrderRequestList(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString(),
            type,
            limit,
            page,
            object : UseCaseResponse<OrderListModel> {
                override fun onSuccess(result: OrderListModel) {
                    Log.i(TAG, "result: $result")
                    ordersLiveData.postValue(result)
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

    fun getOrderDataById(orderId: String) {
        showProgressbar.value = true
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.Default) {
                    orderDetailUseCase.getOrderDataById(
                        SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString(),
                        orderId
                    )
                }
                Log.i(TAG, "result: $data")
                if (data != null) {
                    orderDetailLiveData.postValue(data)
                }
                showProgressbar.value = false
            } catch (e: Exception) {
            }
        }

    }

    fun getInvoice(orderId: String) {
        showProgressbar.value = true
        viewModelScope.launch {
            ordersUseCase.getOrderInvoice(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString(), orderId
            ).let {
                if (it.isSuccessful) {
                    _invoiceLiveData.postValue(Resource.success(it.body()))
                } else {
                    _invoiceLiveData.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = OrdersViewModel::class.java.name
    }
}
