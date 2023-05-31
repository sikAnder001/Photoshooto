package com.photoshooto.ui.job

import androidx.lifecycle.MutableLiveData
import com.photoshooto.base.BaseViewModel
import com.photoshooto.data.source.remote.HomeRepo
import com.photoshooto.domain.model.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class JobViewModel constructor(private val homeRepo: HomeRepo) : BaseViewModel() {

    val dashboardLiveData: MutableLiveData<DashboardDataRS> = MutableLiveData()
    val usersAvailabilityLiveData: MutableLiveData<UserJobListingDataRS> = MutableLiveData()
    val usersJobList: MutableLiveData<UserJobListingDataRS> = MutableLiveData()
    val jobById: MutableLiveData<JobByIdDataRS> = MutableLiveData()
    val photographerCalendar: MutableLiveData<CalendarDataRS> = MutableLiveData()
    val subscriptions: MutableLiveData<SubscriptionsDataRS> = MutableLiveData()
    val couponList: MutableLiveData<BaseResponse> = MutableLiveData()
    val studioList: MutableLiveData<StudioListDataRS> = MutableLiveData()
    val eventTypeList: MutableLiveData<EventTypeDataRS> = MutableLiveData()
    val feedbackUserById: MutableLiveData<FeedbackByIdDataRS> = MutableLiveData()
    val submitFeedback: MutableLiveData<BaseResponse> = MutableLiveData()
    val spamReport: MutableLiveData<BaseResponse> = MutableLiveData()
    val saveFavourite: MutableLiveData<SaveFavouriteDataRS> = MutableLiveData()
    val updateJobStatus: MutableLiveData<BaseResponse> = MutableLiveData()
    val deleteJob: MutableLiveData<BaseResponse> = MutableLiveData()
    val removeFavourite: MutableLiveData<BaseResponse> = MutableLiveData()
    val bookmarkPost: MutableLiveData<GetSavedPostDataRS> = MutableLiveData()
    val myPostedJob: MutableLiveData<UserJobListingDataRS> = MutableLiveData()
    val spamReportDetail: MutableLiveData<SpamReportListDataRS> = MutableLiveData()
    val adminAllJobs: MutableLiveData<UserJobListingDataRS> = MutableLiveData()
    val photographerTypesLiveData: MutableLiveData<PhotographerTypesRS> = MutableLiveData()
    val postJobs: MutableLiveData<SaveJobDataRS> = MutableLiveData()
    val postProductData: MutableLiveData<SavePostProductDataRS> = MutableLiveData()
    val postProductUpdateData: MutableLiveData<SavePostProductDataRS> = MutableLiveData()
    val updatePostJobs: MutableLiveData<BaseResponse> = MutableLiveData()
    val userProfileData: MutableLiveData<UserProfileDataRS> = MutableLiveData()
    val productsList: MutableLiveData<ProductsDataRS> = MutableLiveData()
    val similarProductsList: MutableLiveData<SimilarDataRS> = MutableLiveData()
    val ProductsModel: MutableLiveData<SavePostProductDataRS> = MutableLiveData()
    val categoryList: MutableLiveData<CategoryDataRS> = MutableLiveData()
    val locationList: MutableLiveData<LocationsDataRS> = MutableLiveData()
    val brandList: MutableLiveData<BrandDataRS> = MutableLiveData()


    fun getUserProfile() {
        launch {
            postValue(homeRepo.getUserProfile(), userProfileData)
        }
    }

    fun dashBoardData() {
        launch {
            postValue(homeRepo.getDashboardData(), dashboardLiveData)
        }
    }

    fun usersAvailabilityList(
        page: Int,
        filterJson: JSONObject,
        sortBy: String,
        location: String?
    ) {
        launch {
            postValue(
                homeRepo.getUsersAvailabilityData(page, filterJson, sortBy, location),
                usersAvailabilityLiveData
            )
        }
    }

    fun photographerTypesList(token: String?) {
        launch {
            postValue(homeRepo.getPhotographerTypes(token), photographerTypesLiveData)
        }
    }

    fun userJobListing(page: Int, filterJson: JSONObject, sortBy: String, location: String?) {
        launch {
            postValue(
                homeRepo.getUserJobListingData(page, filterJson, sortBy, location),
                usersJobList
            )
        }
    }

    fun spamReport(userId: String, message: String, type: String,) {
        launch {
            postValue(homeRepo.spamReport(userId, message, type), spamReport)
        }
    }

    fun jobById(jobId: String) {
        launch {
            postValue(homeRepo.getJobByIdData(jobId), jobById)
        }
    }

    fun getFeedbackById(userId: String) {
        launch {
            postValue(homeRepo.getFeedbackById(userId), feedbackUserById)
        }
    }

    fun getCoupons() {
        launch {
            postValue(homeRepo.getCoupons(), couponList)
        }
    }

    fun getEventType(token: String?) {
        launch {
            postValue(homeRepo.getEventType(token), eventTypeList)

        }
    }

    fun getStudioList(currentPage: Int) {
        launch {
            postValue(homeRepo.getStudioList(currentPage), studioList)
        }
    }

    fun getSubscriptions(page: Int) {
        launch {
            postValue(homeRepo.getSubscriptions(page), subscriptions)
        }
    }

    fun getPhotographerCalendar(userId: String) {
        launch {
            postValue(homeRepo.getPhotographerCalendar(userId), photographerCalendar)
        }
    }

    fun saveFavourite(jobId: String) {
        launch {
            postValue(homeRepo.saveFavourite(jobId), saveFavourite)
        }
    }

    fun submitFeedback(rating: Float, message: String, toUser: String) {
        launch {
            postValue(homeRepo.submitFeedback(rating, message, toUser), submitFeedback)
        }
    }

    fun removeFavourite(favId: String) {
        launch {
            postValue(homeRepo.removeFavourite(favId), removeFavourite)
        }
    }

    fun deleteJobById(jobId: String) {
        launch {
            postValue(homeRepo.deleteJobById(jobId), deleteJob)
        }
    }

    fun updateJobStatus(jobId: String, status: String) {
        launch {
            postValue(homeRepo.updateJobStatus(jobId, status), updateJobStatus)
        }
    }

    fun getBookmarkPost(page: Int, sortBy: String) {
        launch {
            postValue(homeRepo.getBookmarkPost(page, sortBy), bookmarkPost)
        }
    }

    fun getMyPostedPost(page: Int, sortBy: String) {
        launch {
            postValue(homeRepo.getMyPostedPost(page, sortBy), myPostedJob)
        }
    }

    fun getSpamReportDetail(page: Int) {
        launch {
            postValue(homeRepo.getSpamReportDetail(page), spamReportDetail)
        }
    }

    fun getAdminAllJobs(page: Int) {
        launch {
            postValue(homeRepo.getAdminAllJobs(page), adminAllJobs)
        }
    }

    fun postJobs(objectPost: PostJobPRQ) {
        launch {
            postValue(homeRepo.postJob(objectPost), postJobs)
        }
    }

    fun postProduct(objectPost: PostProductsPRQ) {
        launch {
            postValue(homeRepo.postProduct(objectPost), postProductData)
        }
    }

    fun productUpdate(id: String, objectPost: PostProductsPRQ) {
        launch {
            postValue(homeRepo.productUpdate(id, objectPost), postProductUpdateData)
        }
    }


    fun updatePost(objectPost: PostJobPRQ, jobId: String) {
        launch {
            postValue(homeRepo.updatePost(objectPost, jobId), updatePostJobs)
        }
    }

    fun productsListing(page: Int) {
        launch {
            postValue(homeRepo.getProductsListingData(page), productsList)
        }
    }

    fun getproductsListing(
        url: MutableMap<String, String>
    ) {
        launch {
            postValue(homeRepo.getproductsListing(url), productsList)
        }
    }

    fun similarProductsListing(id: String) {
        launch {
            postValue(homeRepo.similarProductsListing(id), similarProductsList)
        }
    }

    fun productsDetail(id: String) {
        launch {
            postValue(homeRepo.productsDetail(id), ProductsModel)
        }
    }

    fun categoryListing() {
        launch {
            postValue(homeRepo.getcategoryListingData(), categoryList)
        }
    }

    fun locationListing() {
        launch {
            postValue(homeRepo.locationListing(), locationList)
        }
    }

    fun getBrand() {
        launch {
            postValue(homeRepo.getBrandData(), brandList)
        }
    }


}