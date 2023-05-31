package com.photoshooto.domain.usecase.orders

import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.UpdateStatus
import com.photoshooto.domain.repository.PostsRepository

class OrdersStatusUseCase constructor(
    private val postsRepository: PostsRepository
) : OrdersStatusUseCaseMain<CommonResponse>() {

    override suspend fun updateOrderStatus(
        token: String,
        status: UpdateStatus,
        order_id: String
    ): CommonResponse {
        return postsRepository.updateOrderStatus(token, status, order_id)
    }


}
