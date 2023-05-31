package com.photoshooto.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class FileImagePRQ(
    @SerializedName("category")
    var category: String = "",
    @SerializedName("file")
    var file: File? = null,
    @SerializedName("url")
    var imageUrl: String = "",
) : Parcelable