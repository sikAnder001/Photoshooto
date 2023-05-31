package com.photoshooto.domain.usecase.qr_generation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.*
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.cart.CartDetailsUseCase
import com.photoshooto.domain.usecase.product_details.AddToCartStandeeElement
import com.photoshooto.domain.usecase.product_details.AddTshirtToCartRequest
import com.photoshooto.domain.usecase.product_details.ProductDetailsUseCase
import com.photoshooto.domain.usecase.upload_file.UploadFileUseCase
import com.photoshooto.util.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class QrGenerationViewModel constructor(
    private val qrGenerationUseCase: QrGenerationUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val productDetailUseCase: ProductDetailsUseCase,
    private val cartDetailsUseCase: CartDetailsUseCase
) :
    ViewModel() {

    var isTShirtViewShown = false

    private val _updateImgFileStatus = MutableLiveData<Resource<FileUploadModel>>()
    val updateImgFileStatus: LiveData<Resource<FileUploadModel>> get() = _updateImgFileStatus

    val standeeResponse = MutableLiveData<GetStandeeResponse>()
    val generateQrResponse = MutableLiveData<GenerateQrCodeResponse>()

    val addToCartResponse = MutableLiveData<Resource<CommonResponse>>()
    val removeCartData = MutableLiveData<CommonResponse>()

    val createOrderResponse = MutableLiveData<CreateOrderModel>()
    val cartResponse = MutableLiveData<GetCartDetailsResponse>()
    val addressListResponse = MutableLiveData<GetAddressResponse>()

    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    var filePng: String? = null
    var fileJpg: String? = null
    var fileJpgUri: Uri? = null
    var filePngUri: Uri? = null
    var isLogoPngUploaded = false
    var isLogoJpgUploaded = false
    var uploadedPngUrl: String? = null
    var uploadedJpgUrl: String? = null

    var amountDetails = OrderDetails()
    var address: String? = null

    fun getStandee() {
        showProgressbar.value = true
        qrGenerationUseCase.getStandee(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
            object : UseCaseResponse<GetStandeeResponse> {
                override fun onSuccess(result: GetStandeeResponse) {
                    Log.i(TAG, "result: $result")
                    standeeResponse.value = result
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

    fun generateQrCode() {
        showProgressbar.value = true
        qrGenerationUseCase.generateQrCode(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
            object : UseCaseResponse<GenerateQrCodeResponse> {
                override fun onSuccess(result: GenerateQrCodeResponse) {
                    Log.i(TAG, "result: $result")
                    generateQrResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "result: ${apiError?.getErrorMessage()}")
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun uploadImageFile(
        image: String
    ) {
        val file = File(image)
        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        requestFile.apply {
            val fileUploadMultipart = MultipartBody.Part.createFormData(
                "photos",
                file.name,
                this
            )
            val tShirt: RequestBody = createPartFromString(STANDEE)
            val map: HashMap<String, RequestBody> = HashMap()
            map["category"] = tShirt
            showProgressbar.value = true
            val token = SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)

            viewModelScope.launch {
                uploadFileUseCase.updateImgFile(fileUploadMultipart, map, token).let {
                    if (it.isSuccessful) {
                        _updateImgFileStatus.postValue(Resource.success(it.body()))
                    } else {
                        _updateImgFileStatus.postValue(
                            Resource.error(
                                it.errorBody().toString(),
                                null
                            )
                        )
                    }
                }
            }
        }
    }

    fun addToCart(
        standeeElement: StandeeElement,
        qty: Int,
        altContactNo: String,
        contactNo: String,
        photographerId: String,
        studioAddress: String,
        studioName: String,
        studioTagline: String
    ) {
        val list = ArrayList<String>()
        list.add("$DOMAIN${uploadedPngUrl ?: ""}")
        list.add("$DOMAIN${uploadedJpgUrl ?: ""}")
        standeeElement.apply {
            val standee = AddToCartStandeeElement(
                altContactNo,
                contactNo,
                description,
                discount,
                height,
                id,
                images,
                photographerId,
                price,
                qty,
                studioAddress,
                /*arrayListOf("$DOMAIN${uploadedPngUrl ?: ""}", "$DOMAIN${uploadedJpgUrl ?: ""}")*/
                list,
                studioName,
                studioTagline,
                type,
                weight,
                width
            )

            val request = AddTshirtToCartRequest(standee_list = arrayListOf(standee))
            Log.e("Request", request.toString())
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
            /*productDetailUseCase.addToCart(
                viewModelScope,
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
                request,
                object : UseCaseResponse<CommonResponse> {
                    override fun onSuccess(result: CommonResponse) {
                        Log.i(TAG, "result: $result")
                        addToCartResponse.postValue(result)
                        showProgressbar.value = false
                    }

                    override fun onError(apiError: ApiError?) {
                        Log.i(TAG, "result Add To Cart: ${apiError?.getErrorMessage()}")
                        messageData.value = apiError?.getErrorMessage()
                        showProgressbar.value = false
                        println("praveen apiError " + apiError)
                    }
                }
            )*/
        }
    }

    fun getCartDetails() {
        cartDetailsUseCase.getCartDetails(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
            object : UseCaseResponse<GetCartDetailsResponse> {
                override fun onSuccess(result: GetCartDetailsResponse) {
                    Log.i(TAG, "result: $result")
                    cartResponse.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "result Get Cart Details : ${apiError?.getErrorMessage()}")
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun removeCart() {
        showProgressbar.value = true
        cartDetailsUseCase.removeCartDetails(
            viewModelScope,
            object : UseCaseResponse<CommonResponse> {
                override fun onSuccess(result: CommonResponse) {
                    Log.i(TAG, "result: $result")
                    removeCartData.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "result Get Cart Details : ${apiError?.getErrorMessage()}")
                    messageData.value = apiError?.getErrorMessage()
                    removeCartData.postValue(CommonResponse(message = "", success = false))
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun getUserAddress() {
        cartDetailsUseCase.getUsersAddress(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
            object : UseCaseResponse<GetAddressResponse> {
                override fun onSuccess(result: GetAddressResponse) {
                    Log.i(TAG, "result: $result")
                    addressListResponse.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "result Get User Address : ${apiError?.getErrorMessage()}")
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun createOrder(qrId: String) {
        val request = CreateStandeeOrderRequest(
            delivery_address = address,
            qrcode_id = qrId,
            order_details = amountDetails,
            standee_details = cartResponse.value?.data?.list?.get(0)?.standee_list?.get(0)
        )
        Log.i("orderReq", "$request")
        cartDetailsUseCase.createStandeeOrder(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)!!,
            request,
            object : UseCaseResponse<CreateOrderModel> {
                override fun onSuccess(result: CreateOrderModel) {
                    Log.i(TAG, "result: $result")
                    createOrderResponse.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "result Create Order : ${apiError?.getErrorMessage()}")
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
        private val TAG = QrGenerationViewModel::class.java.name
    }
}
