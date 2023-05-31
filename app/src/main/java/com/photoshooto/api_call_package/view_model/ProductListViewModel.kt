package com.photoshooto.api_call_package.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.domain.model.UpdateProductStatusBody
import com.photoshooto.domain.model.get_productlist_model.ProductListResponse
import com.photoshooto.domain.model.product_by_id.ProductByIdResponse
import kotlinx.coroutines.launch
import retrofit2.Response


class ProductListViewModel(private val repository: Repository2) : ViewModel() {
    val getProductListResponse = MutableLiveData<Response<ProductListResponse>>()
    val getProductByIdResponse = MutableLiveData<Response<ProductByIdResponse>>()
    val approveDeclineResponse = MutableLiveData<Response<ApproveDeclineResponse>>()


   /*  fun getProductList(limit: Int, page: Int,order: Int?) {
       viewModelScope.launch {
           getProductListResponse.value = repository.getProductList(limit, page,order)
       }
   }*/

    fun getProductList(limit: Int, page: Int,order: Int?,sort: String) {
        viewModelScope.launch {
            getProductListResponse.value = repository.getProductList(limit, page,order,sort)
        }
    }


   /* fun getProductList(limit: Int, page: Int,order: Int?,sort: String?,status: String?) {
        viewModelScope.launch {
            getProductListResponse.value = repository.getProductList(limit, page,order,sort,status)
        }
    }*/

    fun updateStatus(status: String, products: UpdateProductStatusBody) {
        viewModelScope.launch {
            approveDeclineResponse.value = repository.updateStatus(status, products)
        }
    }

    fun getProductById(id: String?) {
        viewModelScope.launch {
            getProductByIdResponse.value = repository.getProductId(id)
        }
    }


}