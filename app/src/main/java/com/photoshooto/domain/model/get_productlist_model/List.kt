package com.photoshooto.domain.model.get_productlist_model

import com.google.gson.annotations.SerializedName


data class List(

    @SerializedName("_id") var _id: String? = null,
    @SerializedName("additional_equipment") var additionalEquipment: ArrayList<String> = arrayListOf(),
    @SerializedName("bill_file") var billFile: String? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("condition") var condition: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("equipment_type") var equipmentType: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("images") var images: ArrayList<String> = arrayListOf(),
    @SerializedName("is_bill_available") var isBillAvailable: Boolean? = null,
    @SerializedName("is_in_warranty") var isInWarranty: Boolean? = null,
    @SerializedName("is_negotiable") var isNegotiable: Boolean? = null,
    @SerializedName("is_original_bag_cover") var isOriginalBagCover: Boolean? = null,
    @SerializedName("is_original_battery") var isOriginalBattery: Boolean? = null,
    @SerializedName("is_original_charger") var isOriginalCharger: Boolean? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("offer_price") var offerPrice: Int? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("purchase_date") var purchaseDate: String? = null,
    @SerializedName("selling_location") var sellingLocation: String? = null,
    @SerializedName("service_done") var serviceDone: Boolean? = null,
    @SerializedName("shutter_count") var shutterCount: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("warranty_card_file") var warrantyCardFile: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("is_warranty_card") var isWarrantyCard: Boolean? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("user_profile") var userProfile: UserProfile? = UserProfile()

)