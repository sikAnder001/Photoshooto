package com.photoshooto.domain.model.all_employees_model

import com.google.gson.annotations.SerializedName


data class Location (

  @SerializedName("flat_no"      ) var flatNo      : String?      = null,
  @SerializedName("address"      ) var address     : String?      = null,
  @SerializedName("city"         ) var city        : String?      = null,
  @SerializedName("state"        ) var state       : String?      = null,
  @SerializedName("landmark"     ) var landmark    : String?      = null,
  @SerializedName("gps_location" ) var gpsLocation : GpsLocation? = GpsLocation(),
  @SerializedName("_id"          ) var Id          : String?      = null

)