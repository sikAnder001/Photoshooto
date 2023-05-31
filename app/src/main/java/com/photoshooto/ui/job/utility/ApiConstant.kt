package com.photoshooto.ui.job.utility

class ApiConstant {
    companion object {

        const val prefixAPi = "api/v1"

        const val ApiLogin = "$prefixAPi/login"
        const val ApiSaveJobs = "$prefixAPi/jobs"
        const val ApiPostProduct = "$prefixAPi/products"
        const val ApiPostUpdateProduct = "$prefixAPi/products/id/{Id}"
        const val updateJobStatus = "$prefixAPi/jobs/status/{status}"
        const val userProfile = "$prefixAPi/users/profile"
        const val ApiDashboardData = "$prefixAPi/dashboard"
        const val usersAvailability = "$prefixAPi/jobs/usersAvailability"
        const val userJobListing = "$prefixAPi/jobs/usersJobListing"
        const val productsListing = "$prefixAPi/products"
        const val productsListingUrl = "$prefixAPi/products"
        const val categoryListing = "$prefixAPi/category"
        const val locationsListing = "$prefixAPi/products/locations"
        const val brandListing = "$prefixAPi/brand"
        const val feedbackUserByID = "$prefixAPi/feedbacks/{userId}"
        const val subscriptions = "$prefixAPi/subscriptions"
        const val saveFeedback = "$prefixAPi/feedbacks"
        const val jobDetailByID = "$prefixAPi/jobs/id/{jobId}"
        const val postDetailByID = "$prefixAPi/products/id/{postId}"
        const val similarProducts = "$prefixAPi/products/relevant/{id}"
        const val coupons = "$prefixAPi/couponsList"    //TODO -> Coupouns change
        const val studios = "$prefixAPi/studios"
        const val eventtype = "$prefixAPi/eventtype"
        const val spamreport = "$prefixAPi/spamreport"
        const val saveFavorite = "$prefixAPi/favorite"
        const val removeFavorite = "$prefixAPi/favorite/{favId}"
        const val getBookmarked = "$prefixAPi/favorite/{userId}"
        const val getMyPost = "$prefixAPi/jobs/myJobPosts"
        const val photographersTypeListing = "$prefixAPi/services/photography"
        const val photographerCalendar = "$prefixAPi/jobs/getPhotographerCalendar/{userId}"
    }
}