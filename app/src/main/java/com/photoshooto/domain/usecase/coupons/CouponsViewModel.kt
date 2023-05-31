package com.photoshooto.domain.usecase.coupons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.R
import com.photoshooto.domain.model.CouponData
import com.photoshooto.domain.model.CouponsModel
import kotlinx.coroutines.cancel

class CouponsViewModel() :
    ViewModel() {

    val couponsDetails = MutableLiveData<CouponsModel>()
    val showProgressbar = MutableLiveData<Boolean>()

    fun initTestData() {
        val couponList = ArrayList<CouponData>()
        couponList.add(
            CouponData(
                null,
                "15% OFF on First Order",
                "Valid on orders items worth Rs 999",
                "PSAZADI15"
            )
        )
        couponList.add(
            CouponData(
                R.drawable.ic_temp_coupon_icon,
                "10% OFF using HDFC Bank Credit /Debit Card",
                "Valid on orders items worth Rs 999",
                "HDFC10"
            )
        )
        couponList.add(
            CouponData(
                R.drawable.ic_temp_coupon_icon,
                "10% OFF using AXIS Bank Credit /Debit Card",
                "Valid on orders items worth Rs 999",
                "AXISNEW"
            )
        )
        couponList.add(
            CouponData(
                R.drawable.ic_temp_coupon_icon,
                "10% OFF using SBI Bank Credit /Debit Card",
                "Valid on orders items worth Rs 999",
                "SBINEW"
            )
        )

        val model = CouponsModel(success = true, message = "", data = couponList)
        couponsDetails.value = model
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = CouponsViewModel::class.java.name
    }
}
