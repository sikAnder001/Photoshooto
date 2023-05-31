package com.photoshooto.domain.repository

import com.photoshooto.domain.model.*
import com.photoshooto.ui.job.utility.ApiConstant
import retrofit2.http.*

interface HomeApiService {

    @GET(ApiConstant.userProfile)
    suspend fun getUserProfile(): UserProfileDataRS

    @GET(ApiConstant.ApiDashboardData)
    suspend fun getDashboard(): DashboardDataRS

    @GET(ApiConstant.usersAvailability)
    @JvmSuppressWildcards
    suspend fun getUsersAvailabilityData(
        @Header("Authorization") token: String?,
        @QueryMap requestData: Map<String, Any>,
        @Query("query") location: String?
    ): UserJobListingDataRS

    @GET(ApiConstant.userJobListing)
    @JvmSuppressWildcards
    suspend fun getUserJobListingData(
        @Header("Authorization") token: String?,
        @QueryMap requestData: Map<String, Any>,
        @Query("query") location: String?
    ): UserJobListingDataRS

    @GET(ApiConstant.productsListingUrl)
    suspend fun getproductsListing(
        @Header("Authorization") token: String?,
        @QueryMap options: Map<String, String>
    ): ProductsDataRS

    @GET(ApiConstant.productsListing)
    suspend fun getproductsListingData(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): ProductsDataRS

    @GET(ApiConstant.categoryListing)
    suspend fun categoryListing(
        @Header("Authorization") token: String?
    ): CategoryDataRS

    @GET(ApiConstant.locationsListing)
    suspend fun locationListing(
        @Header("Authorization") token: String?
    ): LocationsDataRS

    @GET(ApiConstant.brandListing)
    suspend fun brandListing(
        @Header("Authorization") token: String?
    ): BrandDataRS

    @GET(ApiConstant.postDetailByID)
    suspend fun productsDetail(
        @Header("Authorization") token: String?,
        @Path("postId") postId: String
    ): SavePostProductDataRS

    @GET(ApiConstant.similarProducts)
    suspend fun similarProductsListing(
        @Header("Authorization") token: String?,
        @Path("id") postId: String
    ): SimilarDataRS

    @GET(ApiConstant.jobDetailByID)
    suspend fun getJobByIdData(
        @Header("Authorization") token: String?,
        @Path("jobId") jobId: String
    ): JobByIdDataRS

    @DELETE(ApiConstant.jobDetailByID)
    suspend fun deleteJobById(
        @Header("Authorization") token: String?,
        @Path("jobId") jobId: String
    ): BaseResponse

    @GET(ApiConstant.eventtype)
    suspend fun getEventType(@Header("Authorization") token: String?): EventTypeDataRS

    @GET(ApiConstant.studios)
    suspend fun getStudioList(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int, @Query("page") page: Int
    ): StudioListDataRS

    @GET(ApiConstant.coupons)
    suspend fun getCoupons(): BaseResponse

    @GET(ApiConstant.feedbackUserByID)
    suspend fun getFeedbackByIdData(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String
    ): FeedbackByIdDataRS

    @GET(ApiConstant.subscriptions)
    suspend fun getSubscriptions(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int, @Query("page") page: Int
    ): SubscriptionsDataRS

    @GET(ApiConstant.photographerCalendar)
    suspend fun getPhotographerCalendar(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String
    ): CalendarDataRS

    @DELETE(ApiConstant.removeFavorite)
    suspend fun removeFavorite(
        @Header("Authorization") token: String?,
        @Path("favId") favId: String
    ): BaseResponse

    @FormUrlEncoded
    @POST(ApiConstant.saveFavorite)
    suspend fun saveFavourite(
        @Header("Authorization") token: String?,
        @Field("job_id") jobId: String
    ): SaveFavouriteDataRS

    @FormUrlEncoded
    @POST(ApiConstant.spamreport)
    suspend fun spamReportUser(
        @Header("Authorization") token: String?,
        @Field("user_id") jobId: String,
        @Field("message") message: String
    ): BaseResponse

    @FormUrlEncoded
    @POST(ApiConstant.spamreport)
    suspend fun spamReportJob(
        @Header("Authorization") token: String?,
        @Field("job_id") jobId: String,
        @Field("message") message: String
    ): BaseResponse

    @GET(ApiConstant.getBookmarked)
    @JvmSuppressWildcards
    suspend fun getBookmarkPost(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String,
        @QueryMap requestData: Map<String, Any>
    ): GetSavedPostDataRS

    @GET(ApiConstant.getMyPost)
    @JvmSuppressWildcards
    suspend fun getMyPostedPost(
        @Header("Authorization") token: String?,
        @QueryMap requestData: Map<String, Any>
    ): UserJobListingDataRS


    @GET(ApiConstant.ApiSaveJobs)
    suspend fun getAdminAllJobs(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int, @Query("page") page: Int, @Query("status") status: String
    ): UserJobListingDataRS

    @GET(ApiConstant.spamreport)
    suspend fun getSpamReportDetail(
        @Header("Authorization") token: String?,
        @Query("limit") limit: Int, @Query("page") page: Int
    ): SpamReportListDataRS

    @GET(ApiConstant.photographersTypeListing)
    suspend fun getPhotographersTypeListingData(@Header("Authorization") token: String?): PhotographerTypesRS

    @POST(ApiConstant.ApiSaveJobs)
    @Headers("Accept: Application/JSON")
    @JvmSuppressWildcards
    suspend fun postJob(
        @Header("Authorization") token: String?,
        @Body() body: Map<String, Any>,
    ): SaveJobDataRS

    @POST(ApiConstant.ApiPostProduct)
    @Headers("Accept: Application/JSON")
    @JvmSuppressWildcards
    suspend fun postProduct(
        @Header("Authorization") token: String?,
        @Body() body: Map<String, Any>,
    ): SavePostProductDataRS

    @PUT(ApiConstant.ApiPostUpdateProduct)
    @Headers("Accept: Application/JSON")
    @JvmSuppressWildcards
    suspend fun productUpdate(
        @Header("Authorization") token: String?,
        @Path("Id") Id: String,
        @Body() body: Map<String, Any>,
    ): SavePostProductDataRS

    @PUT(ApiConstant.jobDetailByID)
    @Headers("Accept: Application/JSON")
    @JvmSuppressWildcards
    suspend fun updatePostJob(
        @Header("Authorization") token: String?,
        @Path("jobId") jobId: String,
        @Body() body: Map<String, Any>,
    ): BaseResponse

    @FormUrlEncoded
    @POST(ApiConstant.ApiSaveJobs)
    suspend fun postAvailability(
        @Header("Authorization") token: String?,
        @Field("job_type") jobType: String,
        @Field("type") type: String,
        @Field("event_type") eventType: String,
        @Field("city") city: ArrayList<String>,
        @Field("mobile") mobile: ArrayList<Long>,
        @Field("start_date") startDate: String,
        @Field("start_time") startTime: String,
        @Field("end_date") endDate: String,
        @Field("end_time") endTime: String,
        @Field("photo_camera_use") photoCameraUse: ArrayList<String>,
        @Field("video_camera_use") videoCameraUse: ArrayList<String>,
        @Field("other_equipments") otherEquipments: ArrayList<String>,
        @Field("description") description: String
    ): SaveJobDataRS

    @FormUrlEncoded
    @PUT(ApiConstant.updateJobStatus)
    suspend fun updateJobStatus(
        @Header("Authorization") token: String?,
        @Field("jobs") jobs: ArrayList<String>,
        @Path("status") status: String,
    ): SaveJobDataRS

    @FormUrlEncoded
    @PUT(ApiConstant.jobDetailByID)
    suspend fun updatePostAvailability(
        @Header("Authorization") token: String?,
        @Path("jobId") jobId: String,
        @Field("job_type") jobType: String,
        @Field("type") type: String,
        @Field("event_type") eventType: String,
        @Field("city") city: ArrayList<String>,
        @Field("mobile") mobile: ArrayList<Long>,
        @Field("start_date") startDate: String,
        @Field("start_time") startTime: String,
        @Field("end_date") endDate: String,
        @Field("end_time") endTime: String,
        @Field("photo_camera_use") photoCameraUse: ArrayList<String>,
        @Field("video_camera_use") videoCameraUse: ArrayList<String>,
        @Field("other_equipments") otherEquipments: ArrayList<String>,
        @Field("description") description: String
    ): BaseResponse

    @FormUrlEncoded
    @POST(ApiConstant.saveFeedback)
    suspend fun submitFeedback(
        @Header("Authorization") token: String?,
        @Field("rating") rating: Float,
        @Field("message") message: String,
        @Field("subject") subject: String,
        @Field("user_id") toUserId: String,
    ): BaseResponse
}