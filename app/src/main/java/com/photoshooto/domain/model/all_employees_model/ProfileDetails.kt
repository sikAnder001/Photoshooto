package com.photoshooto.domain.model.all_employees_model

import com.google.gson.annotations.SerializedName


data class ProfileDetails (

  @SerializedName("name"             ) var name            : String?           = null,
  @SerializedName("first_name"       ) var firstName       : String?           = null,
  @SerializedName("last_name"        ) var lastName        : String?           = null,
  @SerializedName("gender"           ) var gender          : String?           = null,
  @SerializedName("relation"         ) var relation        : String?           = null,
  @SerializedName("birth_date"       ) var birthDate       : String?           = null,
  @SerializedName("experience"       ) var experience      : Int?              = null,
  @SerializedName("profession"       ) var profession      : String?           = null,
  @SerializedName("equipment_use"    ) var equipmentUse    : String?           = null,
  @SerializedName("rates"            ) var rates           : Int?              = null,
  @SerializedName("studio_name"      ) var studioName      : String?           = null,
  @SerializedName("email"            ) var email           : String?           = null,
  @SerializedName("alt_email"        ) var altEmail        : String?           = null,
  @SerializedName("mobile"           ) var mobile          : String?           = null,
  @SerializedName("alt_mobile"       ) var altMobile       : String?           = null,
  @SerializedName("profile_image"    ) var profileImage    : String?           = null,
  @SerializedName("background_image" ) var backgroundImage : String?           = null,
  @SerializedName("language_know"    ) var languageKnow    : ArrayList<String> = arrayListOf(),
  @SerializedName("my_self"          ) var mySelf          : String?           = null,
  @SerializedName("_id"              ) var Id              : String?           = null

)