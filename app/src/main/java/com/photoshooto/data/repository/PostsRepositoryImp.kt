package com.photoshooto.data.repository

import android.util.Log
import com.photoshooto.data.source.remote.ApiService
import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.*
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.repository.PostsRepository
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
import com.photoshooto.util.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response


class PostsRepositoryImp(private val apiService: ApiService) : PostsRepository {

    override suspend fun userLogin(
        body: UserLoginRequest?
    ): Response<UserProfileModel> {
        return apiService.userLogin(body!!)
    }

    override suspend fun sendOtpCall(params: SendOtpRequestModel?): Response<VerifyMovileModel> {
        return apiService.sendMobileOtp(params!!)
    }

    override suspend fun verifyOtp(body: VerifyOTPRequestModel?): UserProfileModel {
        return apiService.verifyOtp(body)
    }

    override suspend fun signupVerifyOtpCall(params: RegisterVerifyOtpModel?): Response<VerifyMovileModel> {
        return apiService.verifyOtpWithGuestData(params!!)
    }

    override suspend fun resetpassword(
        body: PwdChangeRequestModel?,
        token: String?
    ): AllStatusModel {
        return apiService.resetpassword(body, token)
    }

    override suspend fun getUserProfileData(token: String?): UserProfileModel {
        return apiService.getUserProfileData(token)
    }

    override suspend fun getUserProfileByID(
        id: String?,
        token: String?
    ): Response<GetProfileModel> {
        return apiService.getUserProfileByID(id, token)
    }

    override suspend fun getReferral(
        id: String?,
        token: String?
    ): Response<UserReferralCodeModal> {
        return apiService.getUserReferralCode(id, token)
    }

    override suspend fun updateImgFile(
        photos: MultipartBody.Part?, category: HashMap<String, RequestBody>?,
        token: String?
    ): Response<FileUploadModel> {
        return apiService.updateImgFile(
            photos, category,
            token
        )
    }

    override suspend fun updateImgFileArrayList(
        photos: ArrayList<MultipartBody.Part?>, category: HashMap<String, RequestBody>?,
        token: String?
    ): Response<FileUploadModel> {
        return apiService.updateImgFileArrayList(
            photos, category,
            token
        )
    }

    override suspend fun updateUserProfile(
        body: UpdateProfileModel?,
        token: String?
    ): Response<UserProfileModel> {
        return apiService.updateUserProfile(
            body,
            token
        )
    }

    override suspend fun getTshirtList(token: String?, limit: Int, page: Int): ProductDetailsModel {
        return apiService.getTshirtList(
            token,
            limit,
            page
        )
    }

    override suspend fun getOrderRequestList(
        token: String?,
        type: String?,
        limit: Int,
        page: Int
    ): OrderListModel {
        lateinit var result: OrderListModel
        if (type.isNullOrEmpty()) {
            Log.i("orderReq", "token : $token - type  - $type limit - $limit page - $page")
            result = apiService.getOrderRequestList(token)
        } else if (T_SHIRT == type || STANDEE == type) {
            result = apiService.getOrderRequestList(token, type)
        } else if (ACCEPT == type || DECLINE == type || PENDING == type) {
            result = apiService.getOrderSortingList(token, type)
        }
        return result
    }

    override suspend fun getOrderDataById(token: String?, orderID: String?): OrderDetailModel {
        return apiService.getOrderDataByIdDetails(token, orderID)
    }

    override suspend fun updateOrderStatus(
        token: String,
        status: UpdateStatus,
        order_id: String
    ): CommonResponse {
        return apiService.updateOrderStatus(
            status,
            token,
            order_id
        )
    }

    override suspend fun getModulesList(token: String?): GetModulesResponse {
        return apiService.getModulesList(token)
    }

    override suspend fun getReasonsList(token: String?): GetReasonsResponse {
        return apiService.getReasonList(token)
    }

    override suspend fun updateUserStatus(
        token: String?,
        id: String?,
        body: UpdateUserStatusRequest?
    ): CommonResponse {
        return apiService.updateUserStatus(token, id, body)
    }

    override suspend fun removeUser(
        token: String?,
        id: String?,
    ): CommonResponse {
        return apiService.removeUser(token, id)
    }

    override suspend fun getNotification(token: String?, limit: Int, page: Int): NotificationModel {
        return apiService.getNotifications(token!!)

    }


    override suspend fun logout(
        token: String?
    ): Response<AllStatusModel> {
        return apiService.logout(
            token
        )
    }


    override suspend fun addTshirtToCart(
        token: String?,
        body: AddTshirtToCartRequest
    ): Response<CommonResponse> {
        return apiService.addTshirtToCart(
            token,
            body
        )
    }

    override suspend fun addEnquiry(token: String?, body: AddEnquiryReqModel): CommonResponse {
        return apiService.addEnquiryAPI(token!!, body)
    }

    override suspend fun getEnquiries(token: String?): GetEnquiryResponseModel {
        return apiService.getEnquiriesAPI(token)
    }

    override suspend fun createTshirtOrder(
        token: String?,
        body: CreateTshirtOrderRequest
    ): CreateOrderModel {
        return apiService.createTshirtOrder(
            token,
            body
        )
    }

    override suspend fun getCartDetails(token: String?): GetCartDetailsResponse {
        return apiService.getCartDetails(
            token
        )
    }

    override suspend fun removeCartDetails(token: String?): CommonResponse {
        return apiService.removeCart(token)
    }

    override suspend fun getUsersAddress(token: String?): GetAddressResponse {
        return apiService.getUsersAddress(
            token
        )
    }

    override suspend fun getStandee(token: String?): GetStandeeResponse {
        return apiService.getStandee(
            token
        )
    }

    override suspend fun generateQrCode(token: String?): GenerateQrCodeResponse {
        return apiService.generateQrCode(token)
    }

    override suspend fun createStandeeOrder(
        token: String?,
        body: CreateStandeeOrderRequest
    ): CreateOrderModel {
        return apiService.createStandeeOrder(token, body)
    }

    override suspend fun addAddress(token: String?, body: AddAddressRequest): CommonResponse {
        return apiService.addUsersAddress(token, body)
    }


    override suspend fun updateAddress(
        token: String?,
        id: String?,
        body: AddAddressRequest
    ): CommonResponse {
        return apiService.updateUsersAddress(token, id, body)
    }

    override suspend fun deleteUserAddress(
        token: String?,
        id: String?,
    ): CommonResponse {
        return apiService.deleteUsersAddress(token, id)
    }

    override suspend fun deletePlan(
        id: String?,
        token: String?
    ): CommonResponse {
        return apiService.deletePlan(id, token)
    }

    override suspend fun getUsers(
        token: String?,
        limit: Int,
        page: Int,
        role: String
    ): GetUsersResponse {
        return apiService.getUsers(token, limit, page, role)
    }

    override suspend fun getUsers(
        token: String?,
        limit: Int,
        page: Int,
        query: String?,
        sortBy: String?,
        order: String?,
        cityAssigned: String?,
        status: String
    ): GetUsersResponse {
        return apiService.getUsers(token, limit, page, query, sortBy, order, cityAssigned, status)
    }

    override suspend fun getPhotographersServices(token: String?): PhotographerServiceModel {
        return apiService.getPhotographyService(token)
    }

    override suspend fun getReferredUsers(
        token: String?,
        referralCode: String?
    ): UserReferredModel {
        return apiService.getReferredUsers(token, referralCode)
    }

    override suspend fun getPhotographerUsers(token: String?, type: String): GetUsersResponse {
        return apiService.getPhotographerUsers(token, type)
    }

    override suspend fun createUser(token: String?, body: AddUserRequest): CommonResponse {
        return apiService.createUser(token, body)
    }

    override suspend fun getUserDetails(token: String?, id: String?): GetUserDetailsResponse {
        return apiService.getUserDetails(token, id)
    }

    override suspend fun getStatesList(token: String?): GetStatesResponse {
        return apiService.getStatesList(token)
    }

    override suspend fun getCityList(token: String?, cid: String?, sid: String?): GetCityResponse {
        return apiService.getCityList(token)
    }

    override suspend fun getTierCityList(token: String?): GetCityResponse {
        return apiService.getTierCityList(token)
    }

    // Req notification
    override suspend fun reqNotifications(
        params: NotificationRequestModel?,
        token: String?
    ): AllStatusModel {
        return apiService.reqNotifications(
            params!!,
            token
        )
    }

    override suspend fun getFolderList(token: String?): GetFolderResponse {
        return apiService.getFolderList(token)
    }

    override suspend fun getEventOrderHistory(
        token: String?,
        limit: Int,
        page: Int
    ): EventOrderHistoryModel {
        return apiService.getEventOrderHistory(
            token,
            limit,
            page
        )
    }

    override suspend fun callCreateFolder(
        token: String?,
        createFolderRequest: CreateFolderRequest
    ): CommonResponse {
        return apiService.callCreateFolder(token, createFolderRequest)
    }

    override suspend fun getStandeeList(token: String?): GetStandeeResponse {
        return apiService.getStandeeList(token)
    }

    override suspend fun getEventTypesList(token: String?): GetEventTypesResponse {
        return apiService.getEventTypeList(token)
    }

    override suspend fun callCreateEvent(
        token: String?,
        createEventRequest: CreateEventRequest
    ): CommonResponse {
        return apiService.callCreateEvent(token, createEventRequest)
    }

    override suspend fun getEventsList(token: String?): GetEventResponse {
        return apiService.getMyEventList(token)
    }

    override suspend fun getQRCodeList(token: String?): GetQRCodeResponse {
        return apiService.getQRCodeList(token)
    }

    override suspend fun getDashboard(token: String?, userType: String): GetLandingScreenResponse {
        return apiService.getDashboard(token, userType)
    }

    override suspend fun getUserDashboard(token: String?): UserDashboardModel {
        return apiService.getUserDashboard(token)
    }

    override suspend fun paymentSuccess(
        token: String?,
        body: PaymentRequestModel?
    ): Response<AllStatusModel> {
        return apiService.savePaymentStatus(token, body)
    }

    override suspend fun getPlan(
        token: String?,
        limit: Int,
        page: Int
    ): Response<PhotographerPlanResponse> {
        return apiService.getPlan(token, limit, page)
    }

    override suspend fun getPlanByUserId(
        userId: String?,
        token: String?,
        limit: Int,
        page: Int
    ): Response<PhotographerPlanResponse> {
        return apiService.getPlanByUserId(userId, token, limit, page)
    }

    override suspend fun updatePlan(
        id: String?,
        token: String?,
        body: PhotographerPlanBody?,
    ): Response<AllStatusModel> {
        return apiService.updatePlan(id, token, body)
    }

    override suspend fun savePlan(
        token: String?,
        body: PhotographerPlanBody?,
    ): Response<AllStatusModel> {
        return apiService.savePlan(token, body)
    }

    override suspend fun getReview(
        token: String?,
        limit: Int,
        page: Int
    ): Response<ReviewResponse> {
        return apiService.getReview(token, limit, page)
    }

    override suspend fun getReviewByUserId(
        token: String?,
        userId: String?,
        limit: Int,
        page: Int
    ): Response<ReviewByUserIdResponse> {
        return apiService.getReviewByUserId(token, userId, limit, page)
    }


    override suspend fun getWorkDemo(token: String?): Response<WorkDemoResponse> {
        return apiService.getWorkDemo(token)
    }

    override suspend fun getOrderInvoiceAPI(
        token: String?,
        order_id: String?
    ): Response<InvoiceModel> {
        return apiService.getOrderInvoiceAPI(token, order_id)
    }

    override suspend fun sendFeedback(
        token: String?,
        body: SendFeedbackRequest,
    ): Response<AllStatusModel> {
        return apiService.sendFeedback(token, body)
    }

    override suspend fun getJobs(token: String): JobResponse {
        return apiService.getJobs(token)
    }


//    override suspend fun updatePlan(photographerPlanBody: PhotographerPlanBody, token: String?
//    ): Response<AllStatusModel> {
//
//    }

}

//    override suspend fun checkMobileValidation(params: String?): MobileCheckModel {
//        return apiService.getMobileVerify(params!!)
//    }

//    override suspend fun ServiceRegisterRepo(serviceRequest: HashMap<String, RequestBody>?): ServiceResponse {
//        return apiService.ServiceRegisterApi(serviceRequest!!)
//    }

//    override suspend fun LoginTimePhotoUplodRepo(
//        loginFile: MultipartBody.Part?,
//        partMap: HashMap<String, RequestBody>?
//    ): LoginTimePhotoResponse {
//        return apiService.LoginWithPicApi(
//            loginFile!!,
//            partMap
//        )
//    }

//    override suspend fun smsOtpApiRepo(sendSmsRequest: SendSmsRequest?): SmsOtpResponse {
//        return apiService.sendOtpApi(sendSmsRequest!!)
//    }
