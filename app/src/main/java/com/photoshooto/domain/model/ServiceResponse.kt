package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceResponse(
//    val data: Data?=null,
    val message: String = "",
    val success: Boolean
)
//
//@JsonClass(generateAdapter = true)
//data class Data(
//    val channelpatnerData: ChannelpatnerData?,
////    val farmerData: FarmerData? = null,
//    val token: String = "",
////    val feedback: Feedback?,
//    val servicerequest: List<Servicerequest>?
////    val croptypes: List<Croptype>? = null,
////    val typeofservices: List<Typeofservice>? = null
//)
//    val servicerequest: Servicerequest?,

@JsonClass(generateAdapter = true)
data class Feedback(
    val companyName: String = "",
    val created_at: String = "",
    val description: String = "",
    val employee_id: String = "",
    val endTime: String = "",
    val farmer_id: String = "",
    val id: Int = 0,
    var latitude: String? = "",
    var longitude: String? = "",
    val number: String = "",
    val rateourService: String = "",
    val startTime: String = "",
    val typesofServices: String = "",
    val updated_at: String = ""
)

@JsonClass(generateAdapter = true)
data class ChannelpatnerData(
    val addressLine1: String = "",
    val addressLine2: String = "",
    val altMobileNumber: String = "",
    val companyName: String = "",
    val created_at: String = "",
    val employee_id: Int = 0,
    val gst: String = "",
    val id: Int = 0,
    val mobileNumber: String = "",
    val name: String = "",
    val updated_at: String = "",
    val latitude: String? = "",
    val longitude: String? = "",
    val accountNumber: String? = "",
    val bankName: String? = "",
    val branchName: String? = "",
    val ifscCode: String? = "",
    val status: Int? = null,
)


@JsonClass(generateAdapter = true)
data class Servicerequest(
    val advanceBooking: String? = "",
    val advancePayment: String? = "",
    val advancePaymentStatus: String? = "",
    val companyName: String? = "",
    val created_at: String? = "",
    val cropProtectionUsed: String? = "",
    val dateofBooking: String? = "",
    val employee_id: String? = "",
    val farmer_id: String? = "",
    val id: Int = 0,
    val number: String? = "",
    val totalPayment: String? = "",
    val typesofServices: String? = "",
    val updated_at: String? = "",
    val latitude: String? = "",
    val longitude: String? = "",
    val spotBooking: String? = "",
    val status: Int = 0,
    var isSelected: Boolean? = false
)


