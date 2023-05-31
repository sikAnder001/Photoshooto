package com.photoshooto.domain.usecase.upload_file

import com.photoshooto.domain.repository.PostsRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadFileUseCase constructor(
    private val postsRepository: PostsRepository
) {

    suspend fun updateImgFile(
        photos: MultipartBody.Part?,
        category: HashMap<String, RequestBody>?,
        token: String?
    ) =
        postsRepository.updateImgFile(photos, category, token)

    suspend fun updateImgFileArrayList(
        photos: ArrayList<MultipartBody.Part?>,
        category: HashMap<String, RequestBody>?,
        token: String?
    ) =
        postsRepository.updateImgFileArrayList(photos, category, token)
}
