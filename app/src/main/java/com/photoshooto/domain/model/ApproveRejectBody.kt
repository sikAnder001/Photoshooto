package com.photoshooto.domain.model

data class ApproveRejectBody(
    val jobs: ArrayList<String>,
    val remarks: String? = null
)
