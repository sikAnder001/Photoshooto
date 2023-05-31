package com.photoshooto.domain.usecase.orders

import com.photoshooto.domain.repository.PostsRepository

class OrderDetailUseCase constructor(
    private val postsRepository: PostsRepository
) {
    suspend fun getOrderDataById(token: String, orderId: String) =
        postsRepository.getOrderDataById(token, orderId)
}
