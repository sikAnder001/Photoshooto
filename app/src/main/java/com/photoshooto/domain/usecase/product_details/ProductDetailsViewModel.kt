package com.photoshooto.domain.usecase.product_details // ktlint-disable package-name

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.R
import com.photoshooto.domain.model.* // ktlint-disable no-wildcard-imports
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.upload_file.UploadFileUseCase
import com.photoshooto.util.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProductDetailsViewModel constructor(
    private val productDetailUseCase: ProductDetailsUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) :
    ViewModel() {

    private val _updateImgFileStatus = MutableLiveData<Resource<FileUploadModel>>()
    val updateImgFileStatus: LiveData<Resource<FileUploadModel>> get() = _updateImgFileStatus

    val productDetails = MutableLiveData<ProductDetailsModel>()
    val addToCartResponse = MutableLiveData<Resource<CommonResponse>>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    var filePng: String? = null
    var fileJpg: String? = null
    var isLogoPngUploaded = false
    var isLogoJpgUploaded = false
    var uploadedPngUrl: String? = null
    var uploadedJpgUrl: String? = null

    fun getTshirtList(limit: Int, page: Int) {
        showProgressbar.value = true
        productDetailUseCase.getTshirtsList(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
            limit,
            page,
            object : UseCaseResponse<ProductDetailsModel> {
                override fun onSuccess(result: ProductDetailsModel) {
                    Log.i(TAG, "result: $result")
                    productDetails.value = result
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

    fun addTshirtToCart(studioName: String, tagLine: String, selectedSize: TshirtSize) {
        val productObj = productDetails.value?.data?.list?.get(0)
        val list = ArrayList<String>()
        list.add("$DOMAIN${uploadedPngUrl ?: ""}")
        list.add("$DOMAIN${uploadedJpgUrl ?: ""}")
        productObj?.let {
            val item = TshirtElement(
                color = it.color,
                description = it.description,
                discount = it.discount,
                id = it.id,
                images = it.images,
                price = it.price,
                properties = it.properties,
                studio_name = studioName,
                studio_tagline = tagLine,
                studio_logo = /*arrayListOf("$DOMAIN$uploadedPngUrl", "$DOMAIN$uploadedJpgUrl")*/ list,
                sizes = arrayListOf(selectedSize)
            )
            val request = AddTshirtToCartRequest(tshirt_list = arrayListOf(item))
            Log.i("Request", request.toString())
            viewModelScope.launch {
                productDetailUseCase.addToCart(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
                    request
                ).let {
                    Log.i(TAG, "result: $it")
                    if (it.isSuccessful) {
                        addToCartResponse.postValue(Resource.success(it.body()))
                    } else {
                        addToCartResponse.postValue(Resource.error(it.errorBody().toString(), null))
                    }
                }
            }
        }
    }

    fun initTestData() {
        val sizeList = ArrayList<ProductSize>()
        sizeList.add(ProductSize("S", true))
        sizeList.add(ProductSize("M", true))
        sizeList.add(ProductSize("L", true))
        sizeList.add(ProductSize("XL", false))
        sizeList.add(ProductSize("XXL", true))

        val images = ArrayList<Int>()
        images.add(R.drawable.img_temp_product_tshirt)
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

    fun uploadImageFile(
        image: String
    ) {
        val file = File(image)

        val fileUploadMultipart = MultipartBody.Part.createFormData(
            "photos",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )
        val tShirt: RequestBody = createPartFromString(T_SHIRT)
        val map: HashMap<String, RequestBody> = HashMap()
        map["category"] = tShirt
        showProgressbar.value = true

        val token = SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)

        viewModelScope.launch {
            uploadFileUseCase.updateImgFile(fileUploadMultipart, map, token).let {
                if (it.isSuccessful) {
                    _updateImgFileStatus.postValue(Resource.success(it.body()))
                } else {
                    _updateImgFileStatus.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = ProductDetailsViewModel::class.java.name
    }
}
