package com.photoshooto.data.source.remote

import com.photoshooto.base.Either
import com.photoshooto.domain.exception.MyException
import com.photoshooto.domain.model.*
import org.json.JSONObject
import com.photoshooto.domain.model.SpamReportListDataRS as SpamReportListDataRS1


interface HomeRepo {

    suspend fun getUserProfile(): Either<MyException, UserProfileDataRS>
    suspend fun getDashboardData(): Either<MyException, DashboardDataRS>
    suspend fun saveFavourite(jobId: String): Either<MyException, SaveFavouriteDataRS>
    suspend fun getBookmarkPost(page: Int, sortBy: String): Either<MyException, GetSavedPostDataRS>
    suspend fun getAdminAllJobs(page: Int): Either<MyException, UserJobListingDataRS>
    suspend fun getSpamReportDetail(page: Int): Either<MyException, SpamReportListDataRS1>
    suspend fun getMyPostedPost(
        page: Int,
        sortBy: String
    ): Either<MyException, UserJobListingDataRS>

    suspend fun submitFeedback(
        rating: Float,
        message: String,
        toUserId: String
    ): Either<MyException, BaseResponse>

    suspend fun updateJobStatus(jobId: String, status: String): Either<MyException, BaseResponse>
    suspend fun deleteJobById(jobId: String): Either<MyException, BaseResponse>
    suspend fun removeFavourite(jobId: String): Either<MyException, BaseResponse>
    suspend fun getJobByIdData(jobId: String): Either<MyException, JobByIdDataRS>
    suspend fun getPhotographerCalendar(userId: String): Either<MyException, CalendarDataRS>
    suspend fun getSubscriptions(page: Int): Either<MyException, SubscriptionsDataRS>
    suspend fun getCoupons(): Either<MyException, BaseResponse>
    suspend fun getStudioList(currentPage: Int): Either<MyException, StudioListDataRS>
    suspend fun getEventType(token: String?): Either<MyException, EventTypeDataRS>
    suspend fun getFeedbackById(userId: String): Either<MyException, FeedbackByIdDataRS>
    suspend fun getUserJobListingData(
        page: Int,
        filterJson: JSONObject,
        sortBy: String,
        location: String?
    ): Either<MyException, UserJobListingDataRS>

    suspend fun getProductsListingData(
        page: Int
    ): Either<MyException, ProductsDataRS>

    suspend fun getproductsListing(
        url: MutableMap<String, String>
    ): Either<MyException, ProductsDataRS>

    suspend fun similarProductsListing(id: String): Either<MyException, SimilarDataRS>
    suspend fun productsDetail(id: String): Either<MyException, SavePostProductDataRS>
    suspend fun getcategoryListingData(): Either<MyException, CategoryDataRS>
    suspend fun locationListing(): Either<MyException, LocationsDataRS>
    suspend fun getBrandData(): Either<MyException, BrandDataRS>
    suspend fun spamReport(
        userId: String,
        message: String,
        type: String
    ): Either<MyException, BaseResponse>

    suspend fun getUsersAvailabilityData(
        page: Int,
        filterJson: JSONObject,
        sortBy: String,
        location: String?
    ): Either<MyException, UserJobListingDataRS>

    suspend fun getPhotographerTypes(token: String?): Either<MyException, PhotographerTypesRS>
    suspend fun postJob(postJobPRQ: PostJobPRQ): Either<MyException, SaveJobDataRS>
    suspend fun postProduct(postProductsPRQ: PostProductsPRQ): Either<MyException, SavePostProductDataRS>
    suspend fun productUpdate(
        id: String,
        postProductsPRQ: PostProductsPRQ
    ): Either<MyException, SavePostProductDataRS>

    suspend fun updatePost(postJobPRQ: PostJobPRQ, jobId: String): Either<MyException, BaseResponse>
    //  suspend fun performContactUs(contactUsPRQ: ContactUsPRQ): Either<MyException, BaseResponse>

}