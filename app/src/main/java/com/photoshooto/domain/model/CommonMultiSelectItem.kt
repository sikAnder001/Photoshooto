package com.photoshooto.domain.model

data class CommonMultiSelectItem(
    var identifier: String,
    var value: String,
    var isSelected: Boolean? = false
)
