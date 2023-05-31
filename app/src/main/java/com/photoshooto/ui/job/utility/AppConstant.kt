package com.photoshooto.ui.job.utility

import com.photoshooto.domain.model.JobModel

class AppConstant {

    companion object {

        const val pagination_limit = 20
        const val INTENT_SETTINGS = 999

        const val PageStart = 1
        const val page = "page"
        const val page_size = "page_size"
        const val data = "data"
        const val perPage = "per_page"
        const val total = "total"
        const val totalPages = "total_pages"

        const val DB_NAME = "capermint_db"
        const val TABLE_NAME_USER = "users"

        const val id = "id"
        const val avatar = "avatar"
        const val firstName = "first_name"
        const val lastName = "last_name"

        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ID = "extra_id"

        const val authKey = "auth_key"

        const val Yes = "Y"
        const val No = "N"
        const val Space = " "
        const val DeviceTypeAndroid = "A"

        const val user_id = "user_id"
        const val language = "language"
        const val user_type = "user_type"
        const val fullname = "fullname"
        const val email = "email"
        const val phone = "phone"
        const val username = "username"
        const val password = "password"
        const val oldPassword = "old_password"
        const val newPassword = "new_password"
        const val login_type = "login_type"
        const val timezone_detail = "timezone_detail"
        const val app_version = "app_version"
        const val device_type = "device_type"
        const val device_token = "device_token"
        const val device_name = "device_name"
        const val latitude = "latitude"
        const val longitude = "longitude"
        const val profile_pic = "profile_pic"
        const val social_id = "social_id"
        const val enable_push_notifications = "enable_push_notifications"
        const val email_verification_status = "email_verification_status"
        const val phone_verification_status = "phone_verification_status"
        const val is_online = "is_online"
        const val is_active = "is_active"
        const val socket_id = "socket_id"
        const val vTimezone = "vTimezone"
        const val referral_code = "referral_code"
        const val iPhoneVerificationCode = "iPhoneVerificationCode"
        const val search_term = "search_term"
        const val page_code = "page_code"
        const val cmsType = "cmsType"
        const val jobType = "type"
        const val vDeviceUniqueId = "vDeviceUniqueId"
        const val feedback = "feedback"

        lateinit var tmpJobModel: JobModel
        var previewOpen = false
        var orientationPortrait = true


    }
}