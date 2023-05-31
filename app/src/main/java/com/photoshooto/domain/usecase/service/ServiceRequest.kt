package com.photoshooto.domain.usecase.service

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceRequest(
    val farmer_id: String = "",
    val employee_id: String = "",
    val advanceBooking: String = "",
    val dateofBooking: String = "",
    val companyName: String = "",
    val number: String = "",
    val cropProtectionUsed: String = "",
    val typesofServices: String = "",
    val totalPayment: String = "",
    val advancePayment: String = "",
    val advancePaymentStatus: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val employeeType: String = ""
)
