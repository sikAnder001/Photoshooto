package com.photoshooto.domain.usecase.notificationApi

import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.repository.PostsRepository


class NotificationRequestUseCase constructor(
    private val postsRepository: PostsRepository
) : NotificationRequestUseCaseMain<AllStatusModel, NotificationRequestModel?>() {

    override suspend fun reqNotifications(
        params: NotificationRequestModel?,
        token: String?
    ): AllStatusModel {
        return postsRepository.reqNotifications(params, token)
    }

}
