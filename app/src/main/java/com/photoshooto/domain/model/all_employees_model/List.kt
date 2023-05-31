package com.photoshooto.domain.model.all_employees_model

import com.google.gson.annotations.SerializedName


data class List (

  @SerializedName("_id"                 ) var _id                 : String?           = null,
  @SerializedName("id"                  ) var id                 : String?           = null,
  @SerializedName("type"                ) var type               : String?           = null,
  @SerializedName("username"            ) var username           : String?           = null,
  @SerializedName("location"            ) var location           : Location?         = Location(),
  @SerializedName("profile_details"     ) var profileDetails     : ProfileDetails?   = ProfileDetails(),
  @SerializedName("role"                ) var role               : String?           = null,
  @SerializedName("status"              ) var status             : String?           = null,
  @SerializedName("access"              ) var access             : ArrayList<Access> = arrayListOf(),
  @SerializedName("attachments"         ) var attachments        : ArrayList<String> = arrayListOf(),
  @SerializedName("last_active"         ) var lastActive         : String?           = null,
  @SerializedName("online_status"       ) var onlineStatus       : String?           = null,
  @SerializedName("referral_by"         ) var referralBy         : String?           = null,
  @SerializedName("subscriptions_id"    ) var subscriptionsId    : String?           = null,
  @SerializedName("subscriptions_start" ) var subscriptionsStart : String?           = null,
  @SerializedName("subscriptions_end"   ) var subscriptionsEnd   : String?           = null,
  @SerializedName("is_2fa_enabled"      ) var is2faEnabled       : Boolean?          = null,
  @SerializedName("news_letter_enabled" ) var newsLetterEnabled  : Boolean?          = null,
  @SerializedName("employee_by"         ) var employeeBy         : String?           = null,
  @SerializedName("employee_code"       ) var employeeCode       : String?           = null,
  @SerializedName("city_assigned"       ) var cityAssigned       : ArrayList<String> = arrayListOf(),
  @SerializedName("module_assigned"     ) var moduleAssigned     : ArrayList<String> = arrayListOf(),
  @SerializedName("remarks"             ) var remarks            : String?           = null,
  @SerializedName("reasons"             ) var reasons            : ArrayList<String> = arrayListOf(),
  @SerializedName("onboarded_at"        ) var onboardedAt        : String?           = null,
  @SerializedName("reporting_person"    ) var reportingPerson    : String?           = null,
  @SerializedName("is_3rd_party_login"  ) var is3rdPartyLogin    : String?           = null,
  @SerializedName("created_at"          ) var createdAt          : String?           = null,
  @SerializedName("updated_at"          ) var updatedAt          : String?           = null,
  @SerializedName("feedbacks"           ) var feedbacks          : ArrayList<String> = arrayListOf(),
  @SerializedName("plans"               ) var plans              : ArrayList<String> = arrayListOf(),
  @SerializedName("rating"              ) var rating             : Int?              = null,
  @SerializedName("profile_complete"    ) var profileComplete    : Double?           = null

)