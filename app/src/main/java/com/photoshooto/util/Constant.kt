package com.photoshooto.util

object Constant {

    //val RAZOR_PAY_ID = "rzp_test_DcfObto5fBehEJ"
    val RAZOR_PAY_ID = "rzp_live_Kt0uuuyT0klo1u"
    val RAZOR_PAY_SECRET_KEY = "5WVvwDqyzhikTnW9oYo5wvSR"

    val TRUE_CALLER_APP_KEY = "Zku2q1d4c7ccd4e6e495d8090b6fee3854cb4"
    val TRUE_CALLER_FINGERPRINT = "f52adf714b8ac875347b9fc10283b03e25c66ee1"

    val PRIVACY_POLICY_URL = "https://photoshooto.com/privacy-policy"
    val TERMS_N_CONDITION_URL = "https://photoshooto.com/terms-and-conditions"

    enum class USER_TYPE {
        USER,
        PHOTOGRAPHER
    }

    enum class USER_ROLE {
        CLIENT,
        ADMIN,
        SUPERADMIN
    }

    enum class USER_CASE_TYPE {
        USER_CLIENT,
        USER_ADMIN,
        USER_SUPER_ADMIN,
        PHOTOGRAPHER_CLIENT
    }

    enum class EmployeeBy {
        photoshooto, printo
    }


//    enum class USER_CASE_TYPE(val value: String) {
//        USER_CLIENT("user_client"),
//        USER_ADMIN("user_admin"),
//        USER_SUPER_ADMIN("user_super_admin"),
//        PHOTOGRAPHER_CLIENT("photographer_client"),
//    }
}