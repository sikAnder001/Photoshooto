package com.photoshooto.domain.model.reportedListModel

import com.google.gson.annotations.SerializedName


data class ReportedList(

    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id") var productId: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("report_count") var reportCount: Int? = null,
    @SerializedName("user_profile") var userProfile: UserProfile? = UserProfile(),
    @SerializedName("product_details") var productDetails: ProductDetails? = ProductDetails()

)