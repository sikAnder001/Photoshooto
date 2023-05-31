package com.photoshooto.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.photoshooto.ui.job.utility.AppConstant
import kotlinx.parcelize.Parcelize

data class GetSavedPostDataRS(
    @SerializedName("data") val data: BookmarkJobList? = null,
) : BaseResponse()

data class DashboardDataRS(
    @SerializedName("data") var dashboard: DashboardData? = null,
) : BaseResponse()

data class UserProfileDataRS(
    @SerializedName("data") var data: User? = null,
) : BaseResponse()

data class CalendarDataRS(
    @SerializedName("data") var calendar: CalendarData,
) : BaseResponse()

data class JobByIdDataRS(
    @SerializedName("data") val list: List<JobModel> = listOf(),
) : BaseResponse()

data class FeedbackByIdDataRS(
    @SerializedName("data") val data: FeedbackData,
) : BaseResponse()

data class SpamReportListDataRS(
    @SerializedName("data") val data: SpamList,
) : BaseResponse()

data class EventTypeDataRS(
    @SerializedName("data") val data: EventTypeData,
) : BaseResponse()

data class StudioListDataRS(
    @SerializedName("data") val data: StudioListData,
) : BaseResponse()

data class SubscriptionsDataRS(
    @SerializedName("data") val data: SubscriptionData,
) : BaseResponse()

data class SaveFavouriteDataRS(
    @SerializedName("data") val data: ReviewFeedbackModel,
) : BaseResponse()

data class PersonListPRQ(
    @SerializedName("page") var page: Int
)


data class spamReportData(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("job_details")
    val jobDetails: JobModel,
    @SerializedName("job_id")
    val jobId: String = "",
    @SerializedName("report_count")
    val reportCount: Int = 0,
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("user_profile")
    val userProfile: UserProfile = UserProfile()
)


data class StudioObject(
    @SerializedName("address")
    val address: String = "",
    @SerializedName("city")
    val city: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("gps_location")
    val gpsLocation: GpsLocation = GpsLocation(),
    @SerializedName("_id")
    val _id: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("landmark")
    val landmark: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("state")
    val state: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("user_profile")
    val userProfile: UserProfile
)

data class EventType(
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("icon")
    val icon: String = "",
    @SerializedName("_id")
    val _id: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("user_profile")
    val userProfile: UserProfile
)

data class CalendarData(
    @SerializedName("available_calendar") val availableCalendar: List<String> = listOf(),
    @SerializedName("booked_calendar") val bookedCalendar: List<Any> = listOf(),
    @SerializedName("list") val list: List<CalendarEvent> = listOf()
)

data class CalendarEvent(
    @SerializedName("city") val city: List<String> = listOf(),
    @SerializedName("end_date") val endDate: String = "",
    @SerializedName("end_time") val endTime: String = "",
    @SerializedName("id") val id: String = "",
    @SerializedName("evType") var evType: String = "",
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("start_time") val startTime: String = ""
)

data class FeedbackData(
    @SerializedName("overal_rating") val overalRating: Double = 0.0,
    @SerializedName("ratings") val ratings: Ratings = Ratings(),
    @SerializedName("user_reviews") val userReviews: List<ReviewFeedbackModel> = listOf()
)

data class Ratings(
    @SerializedName("5 star") val star5: Int = 0,
    @SerializedName("4 star") val star4: Int = 0,
    @SerializedName("3 star") val star3: Int = 0,
    @SerializedName("2 star") val star2: Int = 0,
    @SerializedName("1 star") val star1: Int = 0
)

data class ReviewFeedbackModel(
    @SerializedName("attachments")
    val attachments: List<Any> = listOf(),
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("delivery")
    val delivery: Boolean = false,
    @SerializedName("dislikes")
    val dislikes: Int = 0,
    @SerializedName("_id")
    val _id: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("likes")
    val likes: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("order_id")
    val orderId: String = "",
    @SerializedName("project_id")
    val projectId: String = "",
    @SerializedName("quality")
    val quality: Boolean = false,
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("response")
    val response: Boolean = false,
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("to_user_id")
    val toUserId: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("user_profile")
    val userProfile: UserProfile
)

data class PersonListRS(
    @SerializedName(AppConstant.data) var personList: List<Person>? = arrayListOf(),
    @SerializedName(AppConstant.page) var page: Int? = 0,
    @SerializedName(AppConstant.perPage) var perPage: Int? = 0,
    @SerializedName(AppConstant.total) var total: Int? = 0,
    @SerializedName(AppConstant.totalPages) var totalPages: Int? = 0
) : BaseResponse()

@Entity(tableName = AppConstant.TABLE_NAME_USER)
data class Person(

    @PrimaryKey(autoGenerate = false) @SerializedName(AppConstant.id) var id: Int = 0,

    @ColumnInfo(name = AppConstant.avatar) @SerializedName(AppConstant.avatar) var avatar: String? = "",

    @ColumnInfo(name = AppConstant.email) @SerializedName(AppConstant.email) var email: String? = "",

    @ColumnInfo(name = AppConstant.firstName) @SerializedName(AppConstant.firstName) var firstName: String? = "",

    @ColumnInfo(name = AppConstant.lastName) @SerializedName(AppConstant.lastName) var lastName: String? = ""
)

/*
data class UsersAvailability(
    @SerializedName("list")
    val list: List<UserList> = listOf(),
    @SerializedName("next_page")
    val nextPage: Int = 0,
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("total_count")
    val total_count: Int = 0
)
*/

data class JobList(
    @SerializedName("list") val list: List<JobModel> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
    @SerializedName("total_count") val total_count: Int = 0
)

data class ProductsList(
    @SerializedName("list") val list: List<ProductsModel> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
    @SerializedName("total_count") val total_count: Int = 0
)

data class SimilarProductsList(
    @SerializedName("data") val list: List<ProductsModel> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
    @SerializedName("total_count") val total_count: Int = 0
)

data class CategoryList(
    @SerializedName("list") val list: ArrayList<CategoryModel> = ArrayList(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
)

data class BrandList(
    @SerializedName("list") val list: ArrayList<BrandModel> = ArrayList(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
)

data class SpamList(
    @SerializedName("list") val list: List<spamReportData> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
)

data class SubscriptionData(
    @SerializedName("list") val list: List<SubscribePlan> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
    @SerializedName("total_count") val total_count: Int = 0
)

data class EventTypeData(
    @SerializedName("list") val list: List<EventType> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
)

data class StudioListData(
    @SerializedName("list") val list: List<StudioObject> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0,
)

data class JobModel(
    @SerializedName("address") var addressObj: Any,
    @SerializedName("book_timings") val bookTimings: BookTimings = BookTimings(),
    @SerializedName("category") val category: String = "",
    @SerializedName("city") val city: List<String> = listOf(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("share_link") val shareLink: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("end_date") val endDate: String = "",
    @SerializedName("end_time") val endTime: String = "",
    @SerializedName("event_type") val eventType: String? = null,
    @SerializedName("favorite") var favorite: List<ReviewFeedbackModel> = listOf(),
    @SerializedName("feedback") val feedbacks: List<FeedbackData> = listOf(),
    @SerializedName("_id") val _id: String = "",
    @SerializedName("id") val id: String = "",
    @SerializedName("is_deleted") val isDeleted: Int = 0,
    @SerializedName("is_verified") var isVerified: Boolean = false,
    @SerializedName("is_favorite") var isFavorite: Boolean = false,
    @SerializedName("min_work_hours") val minWorkHours: Int = 0,
    @SerializedName("mobile") val mobile: List<Long> = listOf(),
    @SerializedName("number_of_photographers") val numberOfPhotographers: Int = 0,
    @SerializedName("other_equipments") val otherEquipments: List<String> = listOf(),
    @SerializedName("photo_camera_use") val photoCameraUse: List<String> = listOf(),
    @SerializedName("photography_type") val photographyType: String = "",
    @SerializedName("pin_code") val pinCode: List<Any> = listOf(),
    @SerializedName("plans") val plans: List<Any> = listOf(),
    @SerializedName("available_slots") val availableSlots: List<String> = listOf(),
    @SerializedName("pricing") val pricing: Int = 0,
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("start_time") val startTime: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("title") val title: String? = null,
    @SerializedName("type") val type: String = "",
    @SerializedName("job_type") var jobType: String = "Free",
    @SerializedName("updated_at") val updatedAt: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("user_profile") val userProfile: UserProfile = UserProfile(),
    @SerializedName("video_camera_use") val videoCameraUse: List<String> = listOf()
)

@Parcelize
data class ProductsModel(
    @SerializedName("_id") val _id: String = "",
    @SerializedName("additional_equipment") val additionalEquipment: ArrayList<String>? = null,
    @SerializedName("bill_file") val bill_file: String = "",
    @SerializedName("brand") val brand: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("condition") val condition: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("equipment_type") val equipmentType: String = "",
    @SerializedName("id") val id: String = "",
    @SerializedName("images") val images: List<String> = listOf(),
    @SerializedName("is_bill_available") val isBillAvailable: Boolean = false,
    @SerializedName("is_in_warranty") val isInWarranty: Boolean = false,
    @SerializedName("is_negotiable") val isNegotiable: Boolean = false,
    @SerializedName("is_original_bag_cover") val isOriginalBagCover: Boolean = false,
    @SerializedName("is_original_battery") val isOriginalBattery: Boolean = false,
    @SerializedName("is_original_charger") val isOriginalCharger: Boolean = false,
    @SerializedName("model") val model: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("offer_price") val offerPrice: Int = 0,
    @SerializedName("price") val price: Int = 0,
    @SerializedName("user_profile") val user_profile: UserProfileOrder? = null,
    @SerializedName("purchase_date") val purchaseDate: String = "",
    @SerializedName("selling_location") val sellingLocation: String = "",
    @SerializedName("service_done") val serviceDone: Boolean = false,
    @SerializedName("shutter_count") val shutterCount: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("warranty_card_file") val warrantyCardFile: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("is_warranty_card") val isWarrantyCard: Boolean = false,
    @SerializedName("status") val status: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
) : Parcelable

data class CategoryModel(
    @SerializedName("_id") val _id: String = "",
    @SerializedName("icon") val icon: String = "",
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("selectCategory") var select: Boolean = false
)


data class BrandModel(
    @SerializedName("_id") val _id: String = "",
    @SerializedName("icon") val icon: String = "",
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("products") val products: List<String> = listOf(),

    )


data class SaveJobDataRS(
    @SerializedName("data") var data: JobModel? = null,
) : BaseResponse()

data class SavePostProductDataRS(
    @SerializedName("data") var data: ProductsModel? = null,
) : BaseResponse()


data class UserJobListingDataRS(
    @SerializedName("data") var data: JobList? = null,
) : BaseResponse()

data class ProductsDataRS(
    @SerializedName("data") var data: ProductsList? = null,
) : BaseResponse()

data class SimilarDataRS(
    @SerializedName("data") val list: List<ProductsModel> = listOf(),
) : BaseResponse()

data class CategoryDataRS(
    @SerializedName("data") var data: CategoryList? = null,
) : BaseResponse()

data class LocationsDataRS(
    @SerializedName("data") var data: ArrayList<String> = ArrayList(),
) : BaseResponse()

data class BrandDataRS(
    @SerializedName("data") var data: BrandList? = null,
) : BaseResponse()

data class DashboardData(
    @SerializedName("available_photographers") val availablePhotographers: List<AvailablePhotographers>,
    @SerializedName("banners") val banners: List<Banner>,
    @SerializedName("cameras_on_sales") val camerasOnSales: List<Any>,
    @SerializedName("jobs_listings") val jobsListings: List<JobModel>,
    @SerializedName("our_achievements") val ourAchievements: List<OurAchievement>,
    @SerializedName("testimonials") val testimonials: List<Testimonial>,
    @SerializedName("top_cities") val topCities: List<TopCity>,
    @SerializedName("top_features") val topFeatures: List<TopFeature>,
    @SerializedName("top_photographer") val topPhotographer: List<TopPhotographer>,
    @SerializedName("top_photography_services") val topPhotographyServices: List<TopPhotographyService>,
    @SerializedName("videos") val videos: List<Video>
)

data class BookTimings(
    @SerializedName("_id") val id: String = "", @SerializedName("user_id") val userId: String = ""
)

data class AddressRQ(
    @SerializedName("_id") val id: String = "",
    @SerializedName("location") val location: String = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
)

open class AddressRQTE(
    var id: String = "",
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)

data class AvailablePhotographers(
    @SerializedName("city") val city: List<String> = listOf(),
    @SerializedName("end_time") val endTime: String = "",
    @SerializedName("event_type") val eventType: String = "",
    @SerializedName("share_link") val shareLink: String = "",
    @SerializedName("id") val id: String = "",
    @SerializedName("min_work_hours") val minWorkHours: Int = 0,
    @SerializedName("mobile") val mobile: List<Long> = listOf(),
    @SerializedName("number_of_photographers") val numberOfPhotographers: Int = 0,
    @SerializedName("other_equipments") val otherEquipments: List<String> = listOf(),
    @SerializedName("photo_camera_use") val photoCameraUse: List<String> = listOf(),
    @SerializedName("pricing") val pricing: Int = 0,
    @SerializedName("start_time") val startTime: String = "",
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("end_date") val endDate: String = "",
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("type") val type: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("user_profile") val userProfile: UserProfile,
    @SerializedName("video_camera_use") val videoCameraUse: List<String> = listOf()
)


data class CameraModel(
    @SerializedName("image_url") val address: String = ""
)

data class TopCity(
    @SerializedName("icon") val icon: String,
    @SerializedName("image") val image: String,
    @SerializedName("name") val name: String,
    @SerializedName("tier_type") val tierType: Int
)

data class TopPhotographer(
    @SerializedName("access") val access: List<Acces>,
    @SerializedName("attachments") val attachments: List<Attachment>,
    @SerializedName("employee_by") val employeeBy: String,
    @SerializedName("employee_code") val employeeCode: String,
    @SerializedName("id") val id: String,
    @SerializedName("is_2fa_enabled") val is2faEnabled: Boolean,
    @SerializedName("location") val location: Location,
    @SerializedName("news_letter_enabled") val newsLetterEnabled: Boolean,
    @SerializedName("online_status") val onlineStatus: String,
    @SerializedName("profile_complete") val profileComplete: Double,
    @SerializedName("profile_details") val profileDetails: ProfileData,
    @SerializedName("rating") val rating: Int,
    @SerializedName("role") val role: String,
    @SerializedName("status") val status: String,
    @SerializedName("type") val type: String,
    @SerializedName("username") val username: String
)

data class TopPhotographyService(
    @SerializedName("icon") val icon: String,
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("__v") val v: Int
)

data class Video(
    @SerializedName("category") val category: String,
    @SerializedName("file_height") val fileHeight: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("file_size") val fileSize: Int,
    @SerializedName("file_type") val fileType: String,
    @SerializedName("file_width") val fileWidth: String,
    @SerializedName("id") val id: String,
    @SerializedName("is_deleted") val isDeleted: Boolean,
    @SerializedName("status") val status: String,
    @SerializedName("type") val type: String,
    @SerializedName("user_id") val userId: String
)

data class BookmarkJob(
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("_id") val _id: String = "",
    @SerializedName("id") val id: String = "",
    @SerializedName("job_id") val jobId: String? = "",
    @SerializedName("photographer_id") val photographerId: String? = "",
    @SerializedName("product_id") val productId: String = "",
    @SerializedName("project_id") val projectId: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("user_profile") val userProfile: UserProfile = UserProfile(),
    @SerializedName("job_details") val jobDetails: JobModel
)

data class BookmarkJobList(
    @SerializedName("list") val list: List<BookmarkJob> = listOf(),
    @SerializedName("next_page") val nextPage: Int = 0,
    @SerializedName("page") val page: Int = 0
)

//photographers type
data class PhotographerTypesRS(
    @SerializedName("data") val photographerTypes: List<PhotographerTypes>
) : BaseResponse()

data class PhotographerTypes(
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("_id") val _id: String,
    @SerializedName("id") val id: String,
    @SerializedName("type") var type: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("__v") val v: Int,
    var isChecked: Boolean = false,
    @SerializedName("other_data") var isOther: Boolean = false,
    @SerializedName("desc") var desc: String = "",
)


data class SubscribePlan(
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("feature_list")
    val featureList: JsonObject,
    @SerializedName("_id")
    val _id: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("offer_price")
    val offerPrice: String = "",
    @SerializedName("year_price")
    var yearPrice: String = "",
    @SerializedName("price")
    val price: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("user_profile")
    val userProfile: UserProfile,
    @SerializedName("year_discount")
    val yearDiscount: String = "",
    @SerializedName("is_selected")
    var isSelected: Boolean = false,
    @SerializedName("recommended")
    var isRecommended: Boolean = false,
    @SerializedName("is_year_selected")
    var isYearSelected: Boolean = false
)

data class FeatureList(
    @SerializedName("free_job_posts")
    val freeJobPosts: Int = 0,
    @SerializedName("free_profile")
    val freeProfile: Boolean = false,
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("instant_notification")
    val instantNotification: Boolean = false,
    @SerializedName("pro_profile_link_in")
    val proProfileLinkIn: Boolean = false,
    @SerializedName("promote_jobs")
    val promoteJobs: Int = 0,
    @SerializedName("total_yearly_savings")
    val totalYearlySavings: Int = 0,
    @SerializedName("verified_job_badge")
    val verifiedJobBadge: Boolean = false,
    @SerializedName("verified_profiles")
    val verifiedProfiles: Boolean = false
)
