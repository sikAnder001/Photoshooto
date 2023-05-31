package com.photoshooto.domain.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotographerServiceModel(
    @SerializedName("data")
    @Expose
    val `data`: List<Data?>?,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("success")
    @Expose
    val success: Boolean?
) {
    data class Data(
        @SerializedName("created_at")
        @Expose
        val createdAt: String?,
        @SerializedName("_id")
        @Expose
        val idMain: String?,
        @SerializedName("id")
        @Expose
        val id: String?,
        @SerializedName("type")
        @Expose
        val type: String?,
        @SerializedName("icon")
        @Expose
        val icon: String?,
        @SerializedName("updated_at")
        @Expose
        val updatedAt: String?,
        @SerializedName("__v")
        @Expose
        val v: Int?
    )
}