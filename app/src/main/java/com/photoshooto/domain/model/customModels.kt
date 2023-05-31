package com.photoshooto.domain.model

import android.os.Parcelable
import com.photoshooto.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectorModel(
    var baseName: String = "",
    var path: String = "",
    var sizeDir: Int = 0,
    var sizeFiles: Int = 0,
    var isDirectory: Boolean = false
) : Parcelable

@Parcelize
data class PaymentListModel(
    var name: String = "",
    var icon: Int = R.drawable.mdi_credit_card_outline,
    var type: String = "",
    var id: Int = 0,
) : Parcelable


@Parcelize
data class CouponModel(
    var name: String = "",
    var id: Int = 0,
) : Parcelable


@Parcelize
data class GalleryModel(
    var baseName: String = "",
    var path: String = "",
    var isLiked: Boolean = false,
    var likeCount: Int = 0,
) : Parcelable

@Parcelize
data class OrderSummaryModel(
    var orderResourceImg: Int = R.drawable.ic_order_subs,
    var orderName: String = "",
    var orderType: String = "",
    var orderPrice: String = "",
    var orderRowTotal: String = "",
) : Parcelable

@Parcelize
class OrderList : ArrayList<OrderSummaryModel>(), Parcelable

data class SpinnerModel(
    var baseName: String = "",
    var isSelected: Boolean = false,
)

data class FeaturePlan(
    var featureId: String = "",
    var featureName: String = "",
    var plan1: String = "",
    var plan2: String = "",
    var plan3: String = "",
    var plan4: String = "",
    var plan1Visible: Boolean = true,
    var selectedPlan: String = "Studio",
)

data class CategoryTypeModel(
    var baseName: String = "",
    var id: String = "",
    var desc: String = "",
    var isChecked: Boolean = false
)