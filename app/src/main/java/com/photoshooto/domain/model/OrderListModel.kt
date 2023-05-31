package com.photoshooto.domain.model


import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SizesItem(
    val quantity: Int = 0,
    val size: String = "",
    val is_avilable: Boolean = false
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserProfileOrder(
    val profession: String = "",
    val pincode: Int = 0,
    val address: String = "",
    val gender: String = "",
    val city: String = "",
    val birth_date: String = "",
    val rates: Int = 0,
    val mobile: Long = 0,
    val experiance: Int = 0,
    val alt_mobile: String?,
    val my_self: String = "",
    val background_image: String = "",
    val gps_location: GpsLocationOrder,
    val studio_name: String = "",
    val profile_image: String = "",
    val flat_no: String = "",
    val name: String = "",
    val alt_email: String = "",
    val _id: String = "",
    val state: String = "",
    val landmark: String = "",
    val equipment_use: String = "",
    val email: String = ""
) : Parcelable

@JsonClass(generateAdapter = true)
data class OrderListModel(
    val data: OrderData?,
    val listItem: ListItem?,
    val success: Boolean = false,
    val message: String = ""
)

@JsonClass(generateAdapter = true)
data class OrderDetailModel(
    val data: ListItem?,
    val success: Boolean = false,
    val message: String = ""
)


@Parcelize
@JsonClass(generateAdapter = true)
data class OrderDetails(
    val coupon_saving: Double = 0.0,
    val shipping: Double = 0.0,
    val total_amount: Double = 0.0,
    val sub_total: Double = 0.0,
    val gst_tax: Double = 0.0,
    val total_units: Double = 0.0,
    val _id: String = ""
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TshirtDetailsItem(
    val images: List<String>?,
    val quantity: Int = 0,
    val color: String = "",
    val description: String = "",
    val discount: Int = 0,
    val contact_number: String = "",
    val studio_logo: List<String>?,
    val studio_name: String = "",
    val alternate_number: String = "",
    val studio_address: String = "",
    val sizes: List<SizesItem>?,
    val price: Int = 0,
    val _id: String = "",
    val studio_tagline: String = "",
    val properties: List<String>?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class GpsLocationOrder(
    val latitude: Int = 0,
    val _id: String = "",
    val longitude: Int = 0
) : Parcelable

@JsonClass(generateAdapter = true)
data class OrderData(
    val next_page: Int = 0,
    val page: Int = 0,
    val list: List<ListItem>?
)

@JsonClass(generateAdapter = true)
/*data class ListItem(
                    val delivery_address: String = "",
                    val coupon_code: String = "",
                    val deliver_address: String = "",
                    val created_at: String = "",
                    val user_profile: UserProfileOrder,
                    val type: String = "",
                    val order_details: OrderDetails,
                    val updated_at: String = "",
                    val user_id: String = "",
                    val tshirt_details: List<TshirtDetailsItem>?,
                    val __v: Int = 0,
                    val _id: String = "",
                    val id: String = "",
                    val status: String = "")*/

@Parcelize
data class ListItem(

    val delivery_address: String? = null,

    val coupon_code: String? = null,

    val deliver_address: String? = null,

    val created_at: String? = null,

    //val project: List<Any?>? = null,

    val user_profile: UserProfileOrder? = null,

    val type: String? = null,

    val order_details: OrderDetails? = null,

    val updated_at: String? = null,

    val user_id: String? = null,

    val tshirt_details: List<TshirtDetailsItem?>? = null,

    val __v: Int? = null,

    //val tracker: List<Any?>? = null,

    //val payment: List<Any?>? = null,

    val _id: String? = null,

    val id: String? = null,

    val standee_details: List<StandeeDetailsItem?>? = null,

    val status: String? = null,

    val order_status: String? = null,

    val project_id: String? = null
) : Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class StandeeDetailsItem(

    val images: List<String>? = null,

    val quantity: Int? = null,

    val description: String? = null,

    val weight: String? = null,

    val discount: Int? = null,

    val type: String? = null,

    val photographer_id: String? = null,

    val contact_number: String? = null,

    val studio_logo: List<String>? = null,

    val studio_name: String? = null,

    val alternate_number: String? = null,

    val studio_address: String? = null,

    val price: Int? = null,

    val alternate_contact_number: String? = null,

    val width: String? = null,

    val _id: String? = null,

    val studio_tagline: String? = null,

    val height: String? = null
) : Parcelable


