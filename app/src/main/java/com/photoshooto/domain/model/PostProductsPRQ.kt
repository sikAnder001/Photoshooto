package com.photoshooto.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class PostProductsPRQ(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description ")
    var description: String = "",
    @SerializedName("brand")
    val brand: String = "",
    @SerializedName("selling_location")
    val sellingLocation: String = "",
    @SerializedName("condition")
    val condition: String = "",
    @SerializedName("purchase_date")
    val purchaseDate: String = "",
    @SerializedName("shutter_count")
    val shutterCount: String = "",
    @SerializedName("is_original_battery")
    var isOriginalBattery: Boolean = false,
    @SerializedName("is_original_charger")
    var isOriginalCharger: Boolean = false,
    @SerializedName("is_original_bag_cover")
    var isOriginalBagCover: Boolean = false,
    @SerializedName("service_done")
    var serviceDone: Boolean = false,
    @SerializedName("additional_equipment")
    var additionalEquipment: ArrayList<String> = ArrayList(),
    @SerializedName("category")
    val category: String = "",
    @SerializedName("is_negotiable")
    val isNegotiable: Boolean = false,
    @SerializedName("is_bill_available")
    var isBillAvailable: Boolean = false,
    @SerializedName("sub_product_brand")
    var sub_product_brand: String = "",
    @SerializedName("sub_product_name")
    var sub_product_name: String = "",
    @SerializedName("bill_file")
    var billFile: String = "",
    @SerializedName("is_in_warranty")
    var isInWarranty: Boolean = false,
    @SerializedName("warranty_card_file")
    var warrantyCardFile: String = "",
    @SerializedName("model")
    val model: String = "",
    @SerializedName("equipment_type")
    val equipment_type: String = "",
    @SerializedName("price")
    var price: Int = 0,
    @SerializedName("offer_price")
    val offerPrice: Int = 0,
    @SerializedName("images")
    var images: ArrayList<String> = ArrayList(),
    @SerializedName("bill")
    var isBill: File? = null,
    @SerializedName("warranty")
    var iswarranty: File? = null,
) : Parcelable
