package com.photoshooto.domain.model.job_by_id

import com.google.gson.annotations.SerializedName


data class Feedback(

    @SerializedName("_id") var _Id: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("rating") var rating: Int? = null,
    @SerializedName("subject") var subject: String? = null,
    @SerializedName("to_user_id") var toUserId: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("__v") var _v: Int? = null,
    @SerializedName("attachments") var attachments: ArrayList<String> = arrayListOf(),
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("delivery") var delivery: Boolean? = null,
    @SerializedName("dislikes") var dislikes: Int? = null,
    @SerializedName("likes") var likes: Int? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("project_id") var projectId: String? = null,
    @SerializedName("quality") var quality: Boolean? = null,
    @SerializedName("response") var response: Boolean? = null,
    @SerializedName("updated_at") var updatedAt: String? = null

)