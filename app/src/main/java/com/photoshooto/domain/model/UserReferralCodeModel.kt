package com.photoshooto.domain.model

class UserReferralCodeModal(
    val `data`: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val _id: String,
        val code: String,
        val created_at: String,
        val referral_url: String,
        val updated_at: String,
        val user_id: String
    )
}
