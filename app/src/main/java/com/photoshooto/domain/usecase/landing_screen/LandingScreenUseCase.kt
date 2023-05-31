package com.photoshooto.domain.usecase.landing_screen

import com.photoshooto.domain.model.GetLandingScreenResponse
import com.photoshooto.domain.model.UserDashboardModel
import com.photoshooto.domain.repository.PostsRepository


class LandingScreenUseCase constructor(
    private val postsRepository: PostsRepository
) : LandingScreenUseCaseMain<GetLandingScreenResponse, UserDashboardModel>() {

    override suspend fun getLandingScreen(
        token: String,
        userType: String
    ): GetLandingScreenResponse {
        return postsRepository.getDashboard(token, userType)
    }

    override suspend fun getUserLandingScreen(token: String): UserDashboardModel {
        return postsRepository.getUserDashboard(token)
    }

}