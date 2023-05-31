package com.photoshooto.data.source.remote

import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.*
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.usecase.cart.CreateTshirtOrderRequest
import com.photoshooto.domain.usecase.login.UserLoginRequest
import com.photoshooto.domain.usecase.manage_address.AddAddressRequest
import com.photoshooto.domain.usecase.manage_admin.AddUserRequest
import com.photoshooto.domain.usecase.manage_admin.UpdateUserRequest
import com.photoshooto.domain.usecase.notificationApi.NotificationRequestModel
import com.photoshooto.domain.usecase.product_details.AddTshirtToCartRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateFolderRequest
import com.photoshooto.domain.usecase.qr_generation.CreateStandeeOrderRequest
import com.photoshooto.domain.usecase.reset_pwd.PwdChangeRequestModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOTPRequestModel
import com.photoshooto.ui.notification.model.NotificationModel
import com.photoshooto.util.API
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    //    @Headers("Content-Type: application/json")
    @POST(API.mobileSendOtp)
    suspend fun sendMobileOtp(@Body body: SendOtpRequestModel): Response<VerifyMovileModel>

    //    @Headers("Content-Type: application/json")
    @POST(API.verifyotp)
    suspend fun verifyOtp(@Body body: VerifyOTPRequestModel?): UserProfileModel

    //    @Headers("Content-Type: application/json")
    @POST(API.resetpassword)
    suspend fun resetpassword(
        @Body body: PwdChangeRequestModel?, @Header("Authorization") auth: String?
    ): AllStatusModel

    //    @Headers("Content-Type: application/json")
    @POST(API.verifyOtpRegisterDetails)
    suspend fun verifyOtpWithGuestData(@Body body: RegisterVerifyOtpModel): Response<VerifyMovileModel>

    //  req notification
//    @Headers("Content-Type: application/json")
    @POST(API.reqNotifications)
    suspend fun reqNotifications(
        @Body body: NotificationRequestModel, @Header("Authorization") auth: String?
    ): AllStatusModel

    // user login

    //    @Headers("Content-Type: application/json")
    @POST(API.userLogin)
    suspend fun userLogin(@Body body: UserLoginRequest): Response<UserProfileModel>

    //    @Headers("Content-Type: application/json")
    @GET(API.getUserProfileData)
    suspend fun getUserProfileData(@Header("Authorization") token: String?): UserProfileModel

    @GET(API.getUserProfileByID + "{id}")
    suspend fun getUserProfileByID(
        @Path("id") id: String?, @Header("Authorization") token: String?
    ): Response<GetProfileModel>


    @GET(API.userReferralCode)
    suspend fun getUserReferralCode(
        @Path("id") id: String?, @Header("Authorization") token: String?
    ): Response<UserReferralCodeModal>


    @Multipart
    @POST(API.updateImgFile)
    suspend fun updateImgFile(
        @Part photos: MultipartBody.Part?,
        @PartMap partMap: HashMap<String, RequestBody>?,
        @Header("Authorization") token: String?
    ): Response<FileUploadModel>

    @Multipart
    @POST(API.updateImgFile)
    suspend fun updateImgFileArrayList(
        @Part photos: ArrayList<MultipartBody.Part?>,
        @PartMap partMap: HashMap<String, RequestBody>?,
        @Header("Authorization") token: String?
    ): Response<FileUploadModel>

    @PUT(API.updateUserProfile)
    suspend fun updateUserProfile(
        @Body body: UpdateProfileModel?, @Header("Authorization") token: String?
    ): Response<UserProfileModel>

    @PUT(API.putOrderStatus + "{order}")
    suspend fun updateOrderStatus(
        @Body body: UpdateStatus?,
        @Header("Authorization") token: String?,
        @Path("order") order_id: String?,
    ): CommonResponse

    @POST(API.addEnquiryAPI)
    suspend fun addEnquiryAPI(
        @Header("Authorization") token: String?,
        @Body body: AddEnquiryReqModel?,
    ): CommonResponse

    @GET(API.getEnquiryAPI)
    suspend fun getEnquiriesAPI(
        @Header("Authorization") token: String?,
    ): GetEnquiryResponseModel

    @GET(API.getOrderInvoice + "{orderID}")
    suspend fun getOrderInvoiceAPI(
        @Header("Authorization") token: String?,
        @Path("orderID") order_id: String?,
    ): Response<InvoiceModel>

    @GET(API.logout)
    suspend fun logout(@Header("Authorization") token: String?): Response<AllStatusModel>

    @GET(API.tShirt)
    suspend fun getTshirtList(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): ProductDetailsModel

    @DELETE(API.cart)
    suspend fun removeCart(
        @Header("Authorization") token: String?
    ): CommonResponse

    @POST(API.cart)
    suspend fun addTshirtToCart(
        @Header("Authorization") token: String?, @Body body: AddTshirtToCartRequest
    ): Response<CommonResponse>

    @POST(API.tShirtOrder)
    suspend fun createTshirtOrder(
        @Header("Authorization") token: String?, @Body body: CreateTshirtOrderRequest
    ): CreateOrderModel

    @GET(API.cart)
    suspend fun getCartDetails(
        @Header("Authorization") token: String?
    ): GetCartDetailsResponse

    @GET(API.address)
    suspend fun getUsersAddress(
        @Header("Authorization") token: String?
    ): GetAddressResponse

    @GET(API.getOrderRequestData)
    suspend fun getOrderRequestList(
        @Header("Authorization") token: String?,
    ): OrderListModel

    @GET(API.getOrderDataById)
    suspend fun getOrderDataByIdDetails(
        @Header("Authorization") token: String?, @Path("id") orderId: String?
    ): OrderDetailModel

    @GET(API.getOrderRequestData)
    suspend fun getOrderRequestList(
        @Header("Authorization") token: String?, @Query("type") type: String?

    ): OrderListModel

    @GET(API.getOrderRequestData)
    suspend fun getOrderSortingList(
        @Header("Authorization") token: String?, @Query("status") type: String?

    ): OrderListModel

    @GET(API.getFolderList)
    suspend fun getFolderList(
        @Header("Authorization") token: String?
    ): GetFolderResponse

    @POST(API.createFolderApi)
    suspend fun callCreateFolder(
        @Header("Authorization") token: String?, @Body createFolderRequest: CreateFolderRequest
    ): CommonResponse

    @GET(API.getStandeeList)
    suspend fun getStandeeList(
        @Header("Authorization") token: String?
    ): GetStandeeResponse

    @GET(API.getEventTypeList)
    suspend fun getEventTypeList(
        @Header("Authorization") token: String?
    ): GetEventTypesResponse

    @POST(API.createEventApi)
    suspend fun callCreateEvent(
        @Header("Authorization") token: String?, @Body createEventRequest: CreateEventRequest
    ): CommonResponse

    @GET(API.getEventsApi)
    suspend fun getMyEventList(
        @Header("Authorization") token: String?
    ): GetEventResponse

    @GET(API.getQRCodeList)
    suspend fun getQRCodeList(
        @Header("Authorization") token: String?
    ): GetQRCodeResponse

    /* @GET(API.getOrderRequestData)
     suspend fun getOrderRequestList(
         @Header("Authorization") token: String?,
         @Query("type") type: String?,
         @Query("limit") limit: Int,
         @Query("page") page: Int
     ): OrderListModel*/


    @GET(API.getEventOrderHistory)
    suspend fun getEventOrderHistory(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): EventOrderHistoryModel

    @POST(API.address)
    suspend fun addUsersAddress(
        @Header("Authorization") token: String?, @Body body: AddAddressRequest
    ): CommonResponse

    @PUT(API.updateAddress)
    suspend fun updateUsersAddress(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
        @Body body: AddAddressRequest
    ): CommonResponse

    @DELETE(API.deleteAddress)
    suspend fun deleteUsersAddress(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
    ): CommonResponse

    @DELETE(API.plan + "/{planId}")
    suspend fun deletePlan(
        @Path("planId") id: String?,
        @Header("Authorization") token: String?
    ): CommonResponse

    @GET(API.standee)
    suspend fun getStandee(
        @Header("Authorization") token: String?
    ): GetStandeeResponse

    @POST(API.qrCode)
    suspend fun generateQrCode(
        @Header("Authorization") token: String?
    ): GenerateQrCodeResponse

    @POST(API.standeeOrder)
    suspend fun createStandeeOrder(
        @Header("Authorization") token: String?, @Body body: CreateStandeeOrderRequest
    ): CreateOrderModel

    @GET(API.jobs)
    suspend fun getJobs(
        @Header("Authorization") token: String?,
    ): JobResponse


    @GET(API.users)
    suspend fun getUsers(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("role") role: String
    ): GetUsersResponse

    @PUT(API.updateUsersStatus)
    suspend fun updateUserStatus(
        @Header("Authorization") token: String?,
        @Path("id") id: String,
        @Body status: UpdateUserRequest,
    ): CommonResponse

    @GET(API.users)
    suspend fun getUsers(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("query") query: String? = null,
        @Query("sort_by") sort_by: String? = null,
        @Query("order") order: String? = null,
        @Query("city_assigned") city_assigned: String? = null,
        @Query("status") status: String
    ): GetUsersResponse

    @GET(API.users)
    suspend fun getPhotographerUsers(
        @Header("Authorization") token: String?,
        @Query("type") type: String
    ): GetUsersResponse

    @POST(API.users)
    suspend fun createUser(
        @Header("Authorization") token: String?, @Body body: AddUserRequest
    ): CommonResponse

    @GET(API.userDetails)
    suspend fun getUserDetails(
        @Header("Authorization") token: String?,
        @Path("u_id") id: String?
    ): GetUserDetailsResponse

    @GET(API.photographyServices)
    suspend fun getPhotographyService(
        @Header("Authorization") token: String?,
    ): PhotographerServiceModel

    @GET(API.referredUsers)
    suspend fun getReferredUsers(
        @Header("Authorization") token: String?,
        @Path("referralCode") referralCode: String?,
    ): UserReferredModel

    @PUT(API.userDetails)
    suspend fun updateUserStatus(
        @Header("Authorization") token: String?,
        @Path("u_id") id: String?,
        @Body body: UpdateUserStatusRequest?
    ): CommonResponse

    @DELETE(API.userDetails)
    suspend fun removeUser(
        @Header("Authorization") token: String?,
        @Path("u_id") id: String?,
    ): CommonResponse

    @GET(API.getStateList)
    suspend fun getStatesList(
        @Header("Authorization") token: String?
    ): GetStatesResponse

    @GET(API.getCityList)
    suspend fun getCityList(
        @Header("Authorization") token: String?,
        @Query("cid") cid: String = "101",
        @Query("sid") sid: String = "4008"
    ): GetCityResponse

    @GET(API.getTierCityList)
    suspend fun getTierCityList(
        @Header("Authorization") token: String?
    ): GetCityResponse

    @GET(API.getModulesList)
    suspend fun getModulesList(
        @Header("Authorization") token: String?
    ): GetModulesResponse

    @GET(API.getReasons)
    suspend fun getReasonList(
        @Header("Authorization") token: String?
    ): GetReasonsResponse

    //region added mazroid
    @GET(API.reqNotifications)
    suspend fun getNotifications(
        @Header("Authorization") auth: String?
    ): NotificationModel
    //endregion

    @GET(API.getDashboard)
    suspend fun getDashboard(
        @Header("Authorization") token: String?, @Query("type") limit: String?
    ): GetLandingScreenResponse

    @GET(API.getDashboard)
    suspend fun getUserDashboard(
        @Header("Authorization") token: String?
    ): UserDashboardModel


    @PUT(API.updatePaymentStatus)
    suspend fun updatePaymentStatus(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
        @Body body: PaymentRequestModel?
    ): Response<AllStatusModel>

    @POST(API.savePayment)
    suspend fun savePaymentStatus(
        @Header("Authorization") token: String?, @Body body: PaymentRequestModel?
    ): Response<AllStatusModel>

    @GET(API.plan)
    suspend fun getPlan(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<PhotographerPlanResponse>

    @GET(API.plan + "/{userId}")
    suspend fun getPlanByUserId(
        @Path("userId") userId: String?,
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<PhotographerPlanResponse>

    @PUT(API.plan + "/{id}")
    suspend fun updatePlan(
        @Path("id") id: String?,
        @Header("Authorization") token: String?,
        @Body photographerPlanBody: PhotographerPlanBody?,
    ): Response<AllStatusModel>

    @POST(API.plan)
    suspend fun savePlan(
        @Header("Authorization") token: String?,
        @Body photographerPlanBody: PhotographerPlanBody?,
    ): Response<AllStatusModel>

    @GET(API.feedbacks)
    suspend fun getReview(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<ReviewResponse>

    @GET(API.feedbacks + "/{u_id}")
    suspend fun getReviewByUserId(
        @Header("Authorization") token: String?,
        @Path("u_id") id: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<ReviewByUserIdResponse>

    @GET(API.workDemo)
    suspend fun getWorkDemo(
        @Header("Authorization") token: String?,
    ): Response<WorkDemoResponse>

    @POST(API.feedbacks)
    suspend fun sendFeedback(
        @Header("Authorization") token: String?,
        @Body sendFeedbackRequest: SendFeedbackRequest,
    ): Response<AllStatusModel>
}
// Send OTP for mobile Validation
//    @POST(API.sendOtp)
//    suspend fun sendOtpApi(
//        @Body sendSmsRequest: SendSmsRequest
//    ): SmsOtpResponse

//    @FormUrlEncoded
//    @POST(API.EMPLOYEE_MOBILE_CHECK)
//    suspend fun getMobileVerify(
//        @Field("mobileNumber") mobileNumber: String
//    ): MobileCheckModel

//    @Multipart
//    @POST(API.SERVICE_REQUEST)
//    suspend fun ServiceRegisterApi(
//        @PartMap partMap: HashMap<String, RequestBody>?
//    ): ServiceResponse

//    @Multipart
//    @POST(API.FILE_UPLOAD)
//    suspend fun LoginWithPicApi(
//        @Part loginphoto: MultipartBody.Part?,
//        @PartMap partMap: HashMap<String, RequestBody>?
//    ): LoginTimePhotoResponse
