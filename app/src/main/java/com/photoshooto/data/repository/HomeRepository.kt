package com.photoshooto.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.photoshooto.base.Either
import com.photoshooto.data.BaseRepository
import com.photoshooto.data.database.AppDao
import com.photoshooto.data.source.remote.HomeRepo
import com.photoshooto.domain.exception.MyException
import com.photoshooto.domain.model.*
import com.photoshooto.domain.repository.HomeApiService
import com.photoshooto.ui.job.utility.AppConstant
import com.photoshooto.ui.job.utility.jsonArrayToArrayList
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import org.json.JSONObject
import java.util.*

class HomeRepository constructor(
    private val homeApiService: HomeApiService,
    private val appDao: AppDao
) : HomeRepo, BaseRepository() {

    override suspend fun getUserProfile(): Either<MyException, UserProfileDataRS> {
        return either {
            homeApiService.getUserProfile()
        }
    }

    override suspend fun getDashboardData(): Either<MyException, DashboardDataRS> {
        return either {
            homeApiService.getDashboard()
        }
    }

    override suspend fun saveFavourite(jobId: String): Either<MyException, SaveFavouriteDataRS> {
        return either {
            homeApiService.saveFavourite(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                jobId
            )
        }
    }

    override suspend fun removeFavourite(favId: String): Either<MyException, BaseResponse> {
        return either {
            homeApiService.removeFavorite(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                favId
            )
        }
    }

    override suspend fun deleteJobById(jobId: String): Either<MyException, BaseResponse> {
        return either {
            homeApiService.deleteJobById(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                jobId
            )
        }
    }

    override suspend fun updateJobStatus(
        jobId: String,
        status: String
    ): Either<MyException, BaseResponse> {
        return either {
            homeApiService.updateJobStatus(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                arrayListOf<String>().apply {
                    add(jobId)
                }, status
            )
        }
    }

    override suspend fun getEventType(token: String?): Either<MyException, EventTypeDataRS> {
        return either {
            homeApiService.getEventType(token)
        }
    }

    override suspend fun getStudioList(currentPage: Int): Either<MyException, StudioListDataRS> {
        return either {
            homeApiService.getStudioList(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                AppConstant.pagination_limit,
                currentPage
            )
        }
    }

    override suspend fun getCoupons(): Either<MyException, BaseResponse> {
        return either {
            homeApiService.getCoupons()
        }
    }

    override suspend fun spamReport(
        userId: String,
        message: String,
        type: String
    ): Either<MyException, BaseResponse> {
        if (type == "user") {
            return either {
                homeApiService.spamReportUser(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    userId, message
                )
            }
        } else {
            return either {
                homeApiService.spamReportJob(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    userId, message
                )
            }
        }
    }

    override suspend fun getJobByIdData(jobId: String): Either<MyException, JobByIdDataRS> {
        return either {
            homeApiService.getJobByIdData(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                jobId
            )
        }
    }

    override suspend fun getFeedbackById(userId: String): Either<MyException, FeedbackByIdDataRS> {
        return either {
            homeApiService.getFeedbackByIdData(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                userId
            )
        }
    }

    override suspend fun getSubscriptions(page: Int): Either<MyException, SubscriptionsDataRS> {
        return either {
            homeApiService.getSubscriptions(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                AppConstant.pagination_limit,
                page
            )
        }
    }

    override suspend fun getPhotographerCalendar(userId: String): Either<MyException, CalendarDataRS> {
        return either {
            homeApiService.getPhotographerCalendar(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                userId
            )
        }
    }

    override suspend fun submitFeedback(
        rating: Float,
        message: String,
        toUserId: String
    ): Either<MyException, BaseResponse> {
        return either {
            homeApiService.submitFeedback(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                rating, message, "Review, good work", toUserId
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun getUserJobListingData(
        page: Int, filterJson: JSONObject, sortBy: String, location: String?
    ): Either<MyException, UserJobListingDataRS> {
        val innerMap: MutableMap<String, Any> = HashMap()
        innerMap["limit"] = AppConstant.pagination_limit
        innerMap["page"] = page
//        innerMap["status"] = "approved"
        Log.e("sortBy", sortBy.toString())
        Log.e("filterJson", filterJson.toString())

        if (filterJson.has("startDate")) {
            innerMap["start_date"] = filterJson.optString("startDate", "")
        }
        if (filterJson.has("endDate")) {
            innerMap["end_date"] = filterJson.optString("endDate", "")
        }

        if (filterJson.has("event_type")) {
            val events = StringJoiner(",")
            for (element in filterJson.getJSONArray("event_type").jsonArrayToArrayList()) {
                events.add(element)
            }
            innerMap["event_type"] = events.toString()
        }

        /* if (filterJson.has("studio_name")) {
             innerMap["studio_name"] = filterJson.getJSONArray("studio_name").jsonArrayToArrayList()
         }*/

        if (filterJson.has("studio")) {
            innerMap["studio"] = filterJson.getJSONArray("studio").jsonArrayToArrayList()
        }

        if (filterJson.has("area")) {
            innerMap["location"] = filterJson.optString("area", "")
        }

        when (sortBy) {
            "Recently Posted" -> {
                innerMap["order"] = -1
                innerMap["sort_by"] = "created_at"
            }
            "Verified" -> {
                innerMap["sort_by"] = "is_verified"
                innerMap["order"] = -1
            }
            "Rating" -> {
                innerMap["sort_by"] = "rating"
                innerMap["order"] = -1
            }
            "Nearby" -> {
                innerMap["sort_by"] = "location"
                innerMap["order"] = -1
            }
            else -> {
                innerMap["sort_by"] = "created_at"
                innerMap["order"] = 1
            }
        }

        return either {
            homeApiService.getUserJobListingData(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                innerMap, location
            )
        }
    }


    override suspend fun getProductsListingData(page: Int): Either<MyException, ProductsDataRS> {
        return either {
            homeApiService.getproductsListingData(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                AppConstant.pagination_limit, page
            )
        }
    }

    override suspend fun getproductsListing(
        url: MutableMap<String, String>
    ): Either<MyException, ProductsDataRS> {
        return either {
            homeApiService.getproductsListing(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN), url
            )
        }
    }

    override suspend fun getcategoryListingData(): Either<MyException, CategoryDataRS> {
        return either {
            homeApiService.categoryListing(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        }
    }

    override suspend fun locationListing(): Either<MyException, LocationsDataRS> {
        return either {
            homeApiService.locationListing(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        }
    }

    override suspend fun getBrandData(): Either<MyException, BrandDataRS> {
        return either {
            homeApiService.brandListing(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
            )
        }
    }

    override suspend fun productsDetail(id: String): Either<MyException, SavePostProductDataRS> {
        return either {
            homeApiService.productsDetail(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN), id
            )
        }
    }

    override suspend fun similarProductsListing(id: String): Either<MyException, SimilarDataRS> {
        return either {
            homeApiService.similarProductsListing(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN), id
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun getUsersAvailabilityData(
        page: Int, filterJson: JSONObject, sortBy: String, location: String?
    ): Either<MyException, UserJobListingDataRS> {
        val innerMap: MutableMap<String, Any> = HashMap()
        innerMap["limit"] = AppConstant.pagination_limit
        innerMap["page"] = page
        innerMap["status"] = "approved"
        Log.e("sortBy", sortBy.toString())
        Log.e("filterJson", filterJson.toString())

        if (filterJson.has("startDate")) {
            innerMap["start_date"] = filterJson.optString("startDate", "")
        }
        if (filterJson.has("endDate")) {
            innerMap["end_date"] = filterJson.optString("endDate", "")
        }

        if (filterJson.has("event_type")) {
            val events = StringJoiner(",")
            for (element in filterJson.getJSONArray("event_type").jsonArrayToArrayList()) {
                events.add(element)
            }
            innerMap["event_type"] = events.toString()
        }

        /* if (filterJson.has("studio_name")) {
             innerMap["studio_name"] = filterJson.getJSONArray("studio_name").jsonArrayToArrayList()
         }*/

        if (filterJson.has("studio")) {
            innerMap["studio"] = filterJson.getJSONArray("studio").jsonArrayToArrayList()
        }

        if (filterJson.has("area")) {
            innerMap["location"] = filterJson.optString("area", "")
        }

        when (sortBy) {
            "Recently Posted" -> {
                innerMap["order"] = -1
                innerMap["sort_by"] = "created_at"
            }
            "Verified" -> {
                innerMap["sort_by"] = "is_verified"
                innerMap["order"] = -1
            }
            "Rating" -> {
                innerMap["sort_by"] = "rating"
                innerMap["order"] = -1
            }
            "Nearby" -> {
                innerMap["sort_by"] = "location"
                innerMap["order"] = -1
            }
            else -> {
                innerMap["sort_by"] = "created_at"
                innerMap["order"] = 1
            }
        }

        return either {
            homeApiService.getUsersAvailabilityData(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                innerMap, location
            )
        }
    }


    override suspend fun getBookmarkPost(
        page: Int,
        sortBy: String
    ): Either<MyException, GetSavedPostDataRS> {
        return either {

            val innerMap: MutableMap<String, Any> = HashMap()
            innerMap["limit"] = AppConstant.pagination_limit
            innerMap["page"] = page

            if (sortBy.equals("Recently Posted", true)) {
                innerMap["createdAt"] = 1
            }
            if (sortBy.equals("Verified", true)) {
                innerMap["status"] = 1
            }
            if (sortBy.equals("Rating", true)) {
                innerMap["rating"] = 1
            }
            if (sortBy.equals("Nearby", true)) {
                innerMap["location"] = 1
            }

            homeApiService.getBookmarkPost(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                SharedPrefsHelper.read(SharedPrefConstant.USER_ID)!!,
                innerMap
            )
        }
    }

    override suspend fun getMyPostedPost(
        page: Int,
        sortBy: String
    ): Either<MyException, UserJobListingDataRS> {
        return either {

            val innerMap: MutableMap<String, Any> = HashMap()
            innerMap["limit"] = AppConstant.pagination_limit
            innerMap["page"] = page

            if (sortBy.equals("Recently Posted", true)) {
                innerMap["createdAt"] = 1
            }
            if (sortBy.equals("Verified", true)) {
                innerMap["status"] = 1
            }
            if (sortBy.equals("Rating", true)) {
                innerMap["rating"] = 1
            }
            if (sortBy.equals("Nearby", true)) {
                innerMap["location"] = 1
            }

            homeApiService.getMyPostedPost(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                innerMap
            )
        }
    }

    override suspend fun getAdminAllJobs(page: Int): Either<MyException, UserJobListingDataRS> {
        return either {
            homeApiService.getAdminAllJobs(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                AppConstant.pagination_limit,
                page, "pending"
            )
        }
    }

    override suspend fun getSpamReportDetail(page: Int): Either<MyException, SpamReportListDataRS> {
        return either {
            homeApiService.getSpamReportDetail(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                AppConstant.pagination_limit,
                page
            )
        }
    }

    override suspend fun getPhotographerTypes(token: String?): Either<MyException, PhotographerTypesRS> {
        return either {
            homeApiService.getPhotographersTypeListingData(token)
        }
    }

    override suspend fun updatePost(
        postJobPRQ: PostJobPRQ,
        jobId: String
    ): Either<MyException, BaseResponse> {
        Log.e("jobIDIDIID", "# ${jobId}")

        if (postJobPRQ.type.equals("hireyou")) {
            // Post Job
            val innerMap: MutableMap<String, Any> = HashMap()
            innerMap["location"] = postJobPRQ.address
            innerMap["latitude"] = postJobPRQ.latitude
            innerMap["longitude"] = postJobPRQ.longitude

            return either {
                homeApiService.updatePostJob(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    jobId = jobId,
                    body = HashMap<String, Any>().apply {
                        put("type", postJobPRQ.type)
                        put("job_type", postJobPRQ.jobType)  //free/promote
                        put("title", postJobPRQ.title)
                        put("mobile", postJobPRQ.mobile)
                        put("photography_type", postJobPRQ.photographyType)
                        put("event_type", postJobPRQ.eventType)
                        put("start_date", postJobPRQ.startDate)
                        put("end_date", postJobPRQ.endDate)
                        put("start_time", postJobPRQ.startTime)
                        put("end_time", postJobPRQ.endTime)
                        put("photo_camera_use", postJobPRQ.cameraUse)
                        put("video_camera_use", postJobPRQ.videoUse)
                        put("other_equipments", postJobPRQ.otherUse)
                        put("number_of_photographers", postJobPRQ.numberOfPhotographers)
                        put("description", postJobPRQ.description)
                        put("address", innerMap)
                    }
                )
            }
        } else {
            //hire me   --> Get Hired as Photographer
            return either {
                homeApiService.updatePostAvailability(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    jobId = jobId,
                    type = postJobPRQ.type,
                    jobType = postJobPRQ.jobType,
                    mobile = postJobPRQ.mobile,
                    startDate = postJobPRQ.startDate,
                    endDate = postJobPRQ.endDate,
                    startTime = postJobPRQ.startTime,
                    endTime = postJobPRQ.endTime,
                    photoCameraUse = postJobPRQ.cameraUse,
                    videoCameraUse = postJobPRQ.videoUse,
                    otherEquipments = postJobPRQ.otherUse,
                    description = postJobPRQ.description,
                    eventType = postJobPRQ.eventType,
                    city = postJobPRQ.city
                )
            }
        }
    }

    override suspend fun postJob(postJobPRQ: PostJobPRQ): Either<MyException, SaveJobDataRS> {
        if (postJobPRQ.type.equals("hireyou")) {
            // Post Job

            val innerMap: MutableMap<String, Any> = HashMap()
            innerMap["location"] = postJobPRQ.address
            innerMap["latitude"] = postJobPRQ.latitude
            innerMap["longitude"] = postJobPRQ.longitude

            return either {
                homeApiService.postJob(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    body = HashMap<String, Any>().apply {
                        put("type", postJobPRQ.type)
                        put("job_type", postJobPRQ.jobType)  //free/promote
                        put("title", postJobPRQ.title)
                        put("mobile", postJobPRQ.mobile)
                        put("photography_type", postJobPRQ.photographyType)
                        put("event_type", postJobPRQ.eventType)
                        put("start_date", postJobPRQ.startDate)
                        put("end_date", postJobPRQ.endDate)
                        put("start_time", postJobPRQ.startTime)
                        put("end_time", postJobPRQ.endTime)
                        put("photo_camera_use", postJobPRQ.cameraUse)
                        put("video_camera_use", postJobPRQ.videoUse)
                        put("other_equipments", postJobPRQ.otherUse)
                        put("number_of_photographers", postJobPRQ.numberOfPhotographers)
                        put("description", postJobPRQ.description)
                        put("address", innerMap)
                    }
                )
            }
        } else {
            //hire me   --> Get Hired as Photographer
            return either {
                homeApiService.postAvailability(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    type = postJobPRQ.type,
                    jobType = postJobPRQ.jobType,
                    mobile = postJobPRQ.mobile,
                    startDate = postJobPRQ.startDate,
                    endDate = postJobPRQ.endDate,
                    startTime = postJobPRQ.startTime,
                    endTime = postJobPRQ.endTime,
                    photoCameraUse = postJobPRQ.cameraUse,
                    videoCameraUse = postJobPRQ.videoUse,
                    otherEquipments = postJobPRQ.otherUse,
                    description = postJobPRQ.description,
                    eventType = postJobPRQ.eventType,
                    city = postJobPRQ.city
                )
            }
        }
    }


    override suspend fun postProduct(postProductsPRQ: PostProductsPRQ): Either<MyException, SavePostProductDataRS> {
        return either {
            homeApiService.postProduct(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                body = HashMap<String, Any>().apply {
                    put("name", postProductsPRQ.name)
                    put("description", postProductsPRQ.description)
                    put("brand", postProductsPRQ.brand)
                    put("selling_location", postProductsPRQ.sellingLocation)
                    put("condition", postProductsPRQ.condition)
                    put("purchase_date", postProductsPRQ.purchaseDate)
                    put("shutter_count", postProductsPRQ.shutterCount)
                    put("is_original_battery", postProductsPRQ.isOriginalBattery)
                    put("is_original_charger", postProductsPRQ.isOriginalCharger)
                    put("is_original_bag_cover", postProductsPRQ.isOriginalBagCover)
                    put("service_done", postProductsPRQ.serviceDone)
                    put("additional_equipment", postProductsPRQ.additionalEquipment)
                    put("category", postProductsPRQ.category)
                    put("is_negotiable", postProductsPRQ.isNegotiable)
                    put("is_bill_available", postProductsPRQ.isBillAvailable)
                    put("bill_file", postProductsPRQ.billFile)
                    put("is_in_warranty", postProductsPRQ.isInWarranty)
                    put("warranty_card_file", postProductsPRQ.warrantyCardFile)
                    put("model", postProductsPRQ.model)
                    put("equipment_type", "")
                    put("price", postProductsPRQ.price)
                    put("offer_price", postProductsPRQ.offerPrice)
                    put("images", postProductsPRQ.images)
                    put("sub_product_name", postProductsPRQ.sub_product_name)
                    put("sub_product_brand", postProductsPRQ.sub_product_brand)
                }
            )
        }
    }

    override suspend fun productUpdate(
        id: String,
        postProductsPRQ: PostProductsPRQ
    ): Either<MyException, SavePostProductDataRS> {
        return either {
            homeApiService.productUpdate(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                Id = id,
                body = HashMap<String, Any>().apply {
                    put("name", postProductsPRQ.name)
                    put("description", postProductsPRQ.description)
                    put("brand", postProductsPRQ.brand)
                    put("selling_location", postProductsPRQ.sellingLocation)
                    put("condition", postProductsPRQ.condition)
                    put("purchase_date", postProductsPRQ.purchaseDate)
                    put("shutter_count", postProductsPRQ.shutterCount)
                    put("is_original_battery", postProductsPRQ.isOriginalBattery)
                    put("is_original_charger", postProductsPRQ.isOriginalCharger)
                    put("is_original_bag_cover", postProductsPRQ.isOriginalBagCover)
                    put("service_done", postProductsPRQ.serviceDone)
                    put("additional_equipment", postProductsPRQ.additionalEquipment)
                    put("category", postProductsPRQ.category)
                    put("is_negotiable", postProductsPRQ.isNegotiable)
                    put("is_bill_available", postProductsPRQ.isBillAvailable)
                    put("bill_file", postProductsPRQ.billFile)
                    put("is_in_warranty", postProductsPRQ.isInWarranty)
                    put("warranty_card_file", postProductsPRQ.warrantyCardFile)
                    put("model", postProductsPRQ.model)
                    put("equipment_type", "")
                    put("price", postProductsPRQ.price)
                    put("offer_price", postProductsPRQ.offerPrice)
                    put("images", postProductsPRQ.images)
                }
            )
        }
    }
}