package com.photoshooto.util

object API {

    const val mobileSendOtp = "v1/sendotp"
    const val verifyOtpRegisterDetails = "v1/register"

    const val reqNotifications = "v1/notifications"
    const val userLogin = "v1/login"
    const val verifyotp = "v1/verifyotp"
    const val resetpassword = "v1/resetpassword"

    const val getUserProfileData = "v1/users/profile"
    const val updateImgFile = "v1/file"
    const val updateUserProfile = "v1/users/profile"
    const val userReferralCode = "v1/referrals/{id}"

    const val getUserProfileByID = "v1/users/id/"
    const val logout = "v1/logout"

    const val tShirt = "v1/tshirt"
    const val cart = "v1/cart"
    const val address = "v1/address"
    const val updateAddress = "v1/address/{id}"
    const val deleteAddress = "v1/address/{id}"
    const val tShirtOrder = "v1/order/tshirt"
    const val standee = "v1/standee"
    const val qrCode = "v1/qrcode"
    const val standeeOrder = "v1/order/standee"


    const val photographyServices = "v1/services/photography"
    const val referredUsers = "v1/users/code/{referralCode}"

    const val users = "v1/users"
    const val updateUsersStatus = "v1/users/id/{id}"
    const val userDetails = "v1/users/id/{u_id}"
    const val getStateList = "v1/geos/states?cid=101"
    const val getCityList = "v1/geos/cities?cid=101&sid=4008"
    const val getTierCityList = "v1/geos/tiercities"

    const val getModulesList = "v1/modules"
    const val getReasons = "v1/reasons"

    const val getOrderDataById = "v1/order/{id}"
    const val getOrderRequestData = "v1/order"
    const val putOrderStatus = "v1/order/"
    const val addEnquiryAPI = "v1/enquiry"
    const val getEnquiryAPI = "v1/enquiry"


    const val getOrderInvoice = "v1/invoice/"

    // const val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IlBTOTNUVDAzTkwiLCJ0b2tlbl9pZCI6IjUxNTdRQkpZMTU3N01EWk4iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE2NjM2NjM2MjN9.euGshDaPISL-pnUReM1_udsGDnd1wsn3SgxFbGBgLJQ"
    const val testToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IlBTNjlIQTgxTEgiLCJ0b2tlbl9pZCI6IjkzNzdBS0xaOTk5N1VZQ0IiLCJyb2xlIjoiY2xpZW50IiwiaWF0IjoxNjY1MDM1NDg0fQ.kggaLI6vt6voKS99J5mein5uECIHbscxq7HiHbcTQLU"

    //QR EVENT ORDER HISTORY
    const val getEventOrderHistory = "v1/events"
    const val getEvents = "v1/events"

    const val getDashboard = "v1/dashboard"
    const val updatePaymentStatus = "v1/payments/id/{id}"
    const val savePayment = "v1/payments"
    const val plan = "v1/plan"
    const val feedbacks = "v1/feedbacks"
    const val workDemo = "v1/workdemo"


    //VS Start
    const val getFolderList = "v1/project"
    const val createFolderApi = "v1/project"
    const val getEventTypeList = "v1/eventtype"
    const val getStandeeList = "v1/order?type=standee"
    const val createEventApi = "v1/events"
    const val getEventsApi = "v1/events"
    const val getQRCodeList = "v1/qrcode"
    const val jobs = "v1/jobs"
    //VS End
}
