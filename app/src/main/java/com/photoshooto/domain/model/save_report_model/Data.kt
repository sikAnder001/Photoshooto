package com.photoshooto.domain.model.save_report_model

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("_id"        ) var _id        : String? = null,
  @SerializedName("id"         ) var id        : String? = null,
  @SerializedName("message"    ) var message   : String? = null,
  @SerializedName("product_id" ) var productId : String? = null,
  @SerializedName("user_id"    ) var userId    : String? = null,
  @SerializedName("_v"        ) var _v        : Int?    = null,
  @SerializedName("created_at" ) var createdAt : String? = null,
  @SerializedName("job_id"     ) var jobId     : String? = null,
  @SerializedName("to_user_id" ) var toUserId  : String? = null,
  @SerializedName("updated_at" ) var updatedAt : String? = null

)