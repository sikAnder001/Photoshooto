//package com.photoshooto.domain.usecase.profile
//
//import com.photoshooto.domain.AllStatusModel
//import com.photoshooto.domain.model.UserProfileModel
//import com.photoshooto.domain.repository.PostsRepository
//
//class GetUserProfileUseCase constructor(
//    private val postsRepository: PostsRepository
//) : GetUserProfileUseCaseMain<UserProfileModel, Any?>() {
//    override suspend fun getUserProfileData(token: String?): UserProfileModel {
//        return postsRepository.getUserProfileData(token)
//    }
//}
