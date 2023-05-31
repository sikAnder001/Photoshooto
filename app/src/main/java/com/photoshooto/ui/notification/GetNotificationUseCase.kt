package com.photoshooto.ui.notification

import com.photoshooto.domain.repository.PostsRepository
import com.photoshooto.ui.notification.model.NotificationModel

class GetNotificationUseCase constructor(
    private val postsRepository: PostsRepository
) : GetNotificationUseCaseMain<NotificationModel, Any?>() {
    override suspend fun getNotificationData(token: String?): NotificationModel {
        return postsRepository.getNotification(token, 10, 0)
    }
}
