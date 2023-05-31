package com.photoshooto.domain.product_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.R
import com.photoshooto.domain.model.ProductDetailsModel
import com.photoshooto.domain.model.ProductSize
import kotlinx.coroutines.cancel

class ProductDetailsViewModel() :
    ViewModel() {

    val productDetails = MutableLiveData<ProductDetailsModel>()
    val showProgressbar = MutableLiveData<Boolean>()

    fun initTestData() {
        val sizeList = ArrayList<ProductSize>()
        sizeList.add(ProductSize("S", true))
        sizeList.add(ProductSize("M", true))
        sizeList.add(ProductSize("L", false))
        sizeList.add(ProductSize("XL", true))

        val images = ArrayList<Int>()
        images.add(R.drawable.img_temp_product_tshirt)
        images.add(R.drawable.img_temp_product_tshirt)
        images.add(R.drawable.img_temp_product_tshirt)

        val details =
            "<p><span style=\"color:#000000\"><span style=\"font-size:14px\">This pure cotton tee will add versatility to your event look.&nbsp;</span></span></p>\n" +
                    "\n" +
                    "<ul>\n" +
                    "\t<li><span style=\"color:#000000\"><span style=\"font-size:14px\"> Navy blue unisex T-shirt</span></span></li>\n" +
                    "\t<li><span style=\"color:#000000\"><span style=\"font-size:14px\"> Solid</span></span></li>\n" +
                    "\t<li><span style=\"color:#000000\"><span style=\"font-size:14px\"> Knitted pure cotton fabric</span></span><br />\n" +
                    "\t&nbsp;</li>\n" +
                    "</ul>"
        // val data = ProductData(images, sizeList, details)

        // val model = ProductDetailsModel(success = true, message = "", data = data)
        // productDetails.value = model
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = ProductDetailsViewModel::class.java.name
    }
}
