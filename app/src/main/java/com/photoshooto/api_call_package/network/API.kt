package com.photoshooto.api_call_package.network

interface API {
    companion object {

        //              const val BASE_URL = "https://app.fitcru.com/fitcru/"
        const val BASE_URL2 = "https://photoshooto.com/api/v1/"

        /*   //               const val IMAGE_BASE_URL = "https://app.fitcru.com/"
           const val IMAGE_BASE_URL = "http://65.1.160.150/"*/

        const val JOBS = "jobs"
        const val JOB_BY_ID = "jobs/id"
        const val SPAM_REPORT = "spamreport"
        const val JOB_STATUS = "jobs/status"
        const val DELETE_JOB = "spamreport/id"

        const val PRODUCT_LIST = "products"
        const val UPDATE_STATUS = "products/status"
        const val REPORTED_LIST = "spamreport"
        const val REMOVE_LIST = "spamreport/id"
        const val PRODUCT_BY_ID = "products/id"
        const val JOB_LIST = "jobs/usersJobListing"

        const val SAVE_SPAM_REPORTS = "spamreport"

        const val MANAGE_EMPLOYEES_TYPE = "users"
        const val EMPLOYEES_DETAILS = "users"




    }

}