package com.photoshooto.domain.repository

import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.*
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.usecase.cart.CreateTshirtOrderRequest
import com.photoshooto.domain.usecase.login.UserLoginRequest
import com.photoshooto.domain.usecase.manage_address.AddAddressRequest
import com.photoshooto.domain.usecase.manage_admin.AddUserRequest
import com.photoshooto.domain.usecase.notificationApi.NotificationRequestModel
import com.photoshooto.domain.usecase.product_details.AddTshirtToCartRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateFolderRequest
import com.photoshooto.domain.usecase.qr_generation.CreateStandeeOrderRequest
import com.photoshooto.domain.usecase.reset_pwd.PwdChangeRequestModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOTPRequestModel
import com.photoshooto.ui.notification.model.NotificationModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface PostsRepository {

    suspend fun sendOtpCall(params: SendOtpRequestModel?): Response<VerifyMovileModel>

    suspend fun reqNotifications(params: NotificationRequestModel?, token: String?): AllStatusModel

    suspend fun userLogin(body: UserLoginRequest?): Response<UserProfileModel>

    suspend fun logout(token: String?): Response<AllStatusModel>

    suspend fun verifyOtp(body: VerifyOTPRequestModel?): UserProfileModel

    suspend fun resetpassword(body: PwdChangeRequestModel?, token: String?): AllStatusModel

    suspend fun signupVerifyOtpCall(params: RegisterVerifyOtpModel?): Response<VerifyMovileModel>

    suspend fun getUserProfileData(token: String?): UserProfileModel

    suspend fun getUserProfileByID(id: String?, token: String?): Response<GetProfileModel>

    suspend fun getReferral(id: String?, token: String?): Response<UserReferralCodeModal>

    suspend fun updateImgFile(
        photos: MultipartBody.Part?,
        category: HashMap<String, RequestBody>?,
        token: String?
    ): Response<FileUploadModel>

    suspend fun updateImgFileArrayList(
        photos: ArrayList<MultipartBody.Part?>,
        category: HashMap<String, RequestBody>?,
        token: String?
    ): Response<FileUploadModel>

    suspend fun updateUserProfile(
        body: UpdateProfileModel?,
        token: String?
    ): Response<UserProfileModel>

    suspend fun getTshirtList(token: String?, limit: Int, page: Int): ProductDetailsModel

    suspend fun addTshirtToCart(
        token: String?,
        body: AddTshirtToCartRequest
    ): Response<CommonResponse>

    suspend fun addEnquiry(
        token: String?,
        body: AddEnquiryReqModel
    ): CommonResponse

    suspend fun getEnquiries(
        token: String?,
    ): GetEnquiryResponseModel

    suspend fun createTshirtOrder(token: String?, body: CreateTshirtOrderRequest): CreateOrderModel

    suspend fun getCartDetails(token: String?): GetCartDetailsResponse

    suspend fun removeCartDetails(token: String?): CommonResponse

    suspend fun getUsersAddress(token: String?): GetAddressResponse

    suspend fun getStandee(token: String?): GetStandeeResponse

    suspend fun generateQrCode(token: String?): GenerateQrCodeResponse

    suspend fun createStandeeOrder(
        token: String?,
        body: CreateStandeeOrderRequest
    ): CreateOrderModel

    suspend fun addAddress(token: String?, body: AddAddressRequest): CommonResponse

    suspend fun updateAddress(token: String?, id: String?, body: AddAddressRequest): CommonResponse
    suspend fun deleteUserAddress(token: String?, id: String?): CommonResponse

    suspend fun getUsers(token: String?, limit: Int, page: Int, role: String): GetUsersResponse

    suspend fun getPhotographersServices(token: String?): PhotographerServiceModel

    suspend fun getReferredUsers(token: String?, referralCode: String?): UserReferredModel

    suspend fun getUsers(
        token: String?,
        limit: Int,
        page: Int,
        query: String? = null,
        sortBy: String? = null,
        order: String? = null,
        cityAssigned: String? = null,
        status: String
    ): GetUsersResponse

    suspend fun getPhotographerUsers(
        token: String?,
        type: String,
    ): GetUsersResponse

    suspend fun createUser(token: String?, body: AddUserRequest): CommonResponse

    suspend fun getUserDetails(token: String?, id: String?): GetUserDetailsResponse

    suspend fun getStatesList(token: String?): GetStatesResponse

    suspend fun updateOrderStatus(
        token: String,
        status: UpdateStatus,
        order_id: String
    ): CommonResponse

    suspend fun getFolderList(token: String?): GetFolderResponse

    suspend fun callCreateFolder(
        token: String?,
        createFolderRequest: CreateFolderRequest
    ): CommonResponse

    suspend fun getStandeeList(token: String?): GetStandeeResponse

    suspend fun getEventTypesList(token: String?): GetEventTypesResponse

    suspend fun callCreateEvent(
        token: String?,
        createEventRequest: CreateEventRequest
    ): CommonResponse

    suspend fun getEventsList(token: String?): GetEventResponse

    suspend fun getQRCodeList(token: String?): GetQRCodeResponse

    suspend fun getCityList(token: String?, cid: String?, sid: String?): GetCityResponse

    suspend fun getTierCityList(token: String?): GetCityResponse

    suspend fun getOrderRequestList(
        token: String?,
        type: String?,
        limit: Int,
        page: Int
    ): OrderListModel

    suspend fun getOrderDataById(token: String?, orderID: String?): OrderDetailModel

    suspend fun getModulesList(token: String?): GetModulesResponse

    suspend fun getReasonsList(token: String?): GetReasonsResponse

    suspend fun updateUserStatus(
        token: String?,
        id: String?,
        body: UpdateUserStatusRequest?
    ): CommonResponse

    suspend fun removeUser(
        token: String?,
        id: String?,
    ): CommonResponse

    suspend fun getNotification(token: String?, limit: Int, page: Int): NotificationModel

    suspend fun getEventOrderHistory(token: String?, limit: Int, page: Int): EventOrderHistoryModel

    suspend fun getDashboard(token: String?, userType: String): GetLandingScreenResponse

    suspend fun getUserDashboard(token: String?): UserDashboardModel

    suspend fun paymentSuccess(
        token: String?,
        body: PaymentRequestModel?
    ): Response<AllStatusModel>

    suspend fun getPlan(token: String?, limit: Int, page: Int): Response<PhotographerPlanResponse>

    suspend fun getPlanByUserId(
        userId: String?,
        token: String?,
        limit: Int,
        page: Int
    ): Response<PhotographerPlanResponse>

    suspend fun updatePlan(
        id: String?,
        token: String?,
        body: PhotographerPlanBody?
    ): Response<AllStatusModel>

    suspend fun savePlan(token: String?, body: PhotographerPlanBody?): Response<AllStatusModel>

    suspend fun deletePlan(id: String?, token: String?): CommonResponse

    suspend fun getReview(token: String?, limit: Int, page: Int): Response<ReviewResponse>

    suspend fun getReviewByUserId(
        token: String?,
        userId: String?,
        limit: Int,
        page: Int
    ): Response<ReviewByUserIdResponse>

    suspend fun getWorkDemo(token: String?): Response<WorkDemoResponse>

    suspend fun getOrderInvoiceAPI(token: String?, order_id: String?): Response<InvoiceModel>

    suspend fun sendFeedback(
        token: String?,
        sendFeedbackRequest: SendFeedbackRequest
    ): Response<AllStatusModel>

    suspend fun getJobs(token: String): JobResponse


//    suspend fun smsOtpApiRepo(sendSmsRequest: SendSmsRequest?): SmsOtpResponse//

//    suspend fun checkMobileValidation(params: String?): MobileCheckModel
//
//    suspend fun ServiceRegisterRepo(partMap: HashMap<String, RequestBody>?): ServiceResponse
//
//    suspend fun LoginTimePhotoUplodRepo(
//        loginFile: MultipartBody.Part?,
//        partMap: HashMap<String, RequestBody>?
//    ): LoginTimePhotoResponse
}