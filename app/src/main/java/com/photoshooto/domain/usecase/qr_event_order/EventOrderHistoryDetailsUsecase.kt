package com.photoshooto.domain.usecase.qr_event_order

import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.EventOrderHistoryModel
import com.photoshooto.domain.repository.PostsRepository

class EventOrderHistoryDetailsUseCase constructor(
    private val postsRepository: PostsRepository
) : EventOrderHistoryDetailsUseCaseMain<EventOrderHistoryModel, CommonResponse>() {

    override suspend fun getEventOrderHistory(
        token: String,
        limit: Int,
        page: Int
    ): EventOrderHistoryModel {
        return postsRepository.getEventOrderHistory(token, limit, page)
    }

}