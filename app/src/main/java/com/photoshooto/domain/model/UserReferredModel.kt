package com.photoshooto.domain.model


data class UserReferredModel(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
) {

    data class Data(
        val _id: String,
        val access: List<Acces>,
        val attachments: List<Any>,
        val city_assigned: List<Any>,
        val created_at: String,
        val employee_by: String,
        val employee_code: String,
        val id: String,
        val is_2fa_enabled: Boolean,
        val is_3rd_party_login: String,
        val last_active: Any,
        val location: Location,
        val module_assigned: List<Any>,
        val news_letter_enabled: Boolean,
        val onboarded_at: Any,
        val online_status: String,
        val profile_details: ProfileDetails,
        val reasons: List<Any>,
        val referral_by: String,
        val remarks: String,
        val reporting_person: String,
        val role: String,
        val status: String,
        val subscriptions_end: Any,
        val subscriptions_id: String,
        val subscriptions_start: Any,
        val type: String,
        val updated_at: String,
        val username: String
    )

    data class Acces(
        val entity: String,
        val permission: String
    )

    data class Location(
        val _id: String,
        val address: String,
        val city: String,
        val flat_no: String,
        val gps_location: GpsLocation,
        val landmark: String,
        val state: String
    )

    data class ProfileDetails(
        val _id: String,
        val alt_email: String,
        val alt_mobile: String,
        val background_image: String,
        val birth_date: String,
        val email: String,
        val equipment_use: String,
        val experience: Int,
        val first_name: String,
        val gender: String,
        val language_know: List<Any>,
        val mobile: String,
        val my_self: String,
        val name: String,
        val profession: String,
        val profile_image: String,
        val rates: Int,
        val relation: String,
        val studio_name: String
    )

    data class GpsLocation(
        val _id: String,
        val latitude: Int,
        val longitude: Int
    )
}
