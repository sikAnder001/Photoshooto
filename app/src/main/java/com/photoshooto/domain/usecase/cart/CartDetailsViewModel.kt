package com.photoshooto.domain.usecase.cart

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.CreateOrderModel
import com.photoshooto.domain.model.GetAddressResponse
import com.photoshooto.domain.model.GetCartDetailsResponse
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.product_details.TshirtSize
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.showToast
import kotlinx.coroutines.cancel

class CartDetailsViewModel constructor(private val cartDetailsUseCase: CartDetailsUseCase) :
    ViewModel() {

    val createOrderResponse = MutableLiveData<CreateOrderModel>()
    val cartResponse = MutableLiveData<GetCartDetailsResponse>()
    val addressListResponse = MutableLiveData<GetAddressResponse>()

    // val cartDetails = MutableLiveData<CartDetailsModel>()
    val messageData = MutableLiveData<String>()
    val showProgressbar = MutableLiveData<Boolean>()
    var selectedCouponCode: String? = null
    var availableSize: ArrayList<TshirtSize>? = null
    var address: String? = null
    var amountDetails = OrderDetails()

    fun createOrder(requireContext: Context) {
        try {
            var data = cartResponse.value?.data?.list?.get(0)?.tshirt_list?.get(0)
        } catch (e: Exception) {
            showToast(requireContext, "Can't proceed total amount is Empty")
            return
        }
        val request = CreateTshirtOrderRequest(
            delivery_address = address,
            order_details = amountDetails,
            tshirt_details = cartResponse.value?.data?.list?.get(0)?.tshirt_list?.get(0)
        )
        cartDetailsUseCase.createTshirtOrder(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            request,
            object : UseCaseResponse<CreateOrderModel> {
                override fun onSuccess(result: CreateOrderModel) {
                    Log.i(TAG, "result: $result")
                    createOrderResponse.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("praveen apiError " + apiError)
                }
            }
        )
    }

    fun getCartDetails() {
        cartDetailsUseCase.getCartDetails(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetCartDetailsResponse> {
                override fun onSuccess(result: GetCartDetailsResponse) {
                    Log.i(TAG, "result: $result")
                    cartResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("praveen apiError " + apiError)
                }
            }
        )
    }

    fun getUserAddress() {
        cartDetailsUseCase.getUsersAddress(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetAddressResponse> {
                override fun onSuccess(result: GetAddressResponse) {
                    Log.i(TAG, "result: $result")
                    addressListResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("praveen apiError " + apiError)
                }
            }
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = CartDetailsViewModel::class.java.name
    }
}
