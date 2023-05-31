//package com.photoshooto.domain.usecase.profile
//
//import com.photoshooto.domain.AllStatusModel
//import com.photoshooto.domain.model.UpdateProfileModel
//import com.photoshooto.domain.model.UserProfileModel
//import com.photoshooto.domain.repository.PostsRepository
//import okhttp3.RequestBody
//
//class UpdateProfileUseCase constructor(
//    private val postsRepository: PostsRepository
//) : UpdateProfileUseCaseMain<UserProfileModel, Any?>() {
//    override suspend fun updateUserProfile(body: UpdateProfileModel?, token: String?): UserProfileModel {
//        return postsRepository.updateUserProfile(body,token)
//    }
//}
