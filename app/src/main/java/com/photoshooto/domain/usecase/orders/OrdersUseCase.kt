package com.photoshooto.domain.usecase.orders

import com.photoshooto.domain.model.OrderListModel
import com.photoshooto.domain.repository.PostsRepository

class OrdersUseCase constructor(
    private val postsRepository: PostsRepository
) : OrdersUseCaseMain<OrderListModel>() {
    override suspend fun getOrderRequestList(
        token: String,
        type: String,
        limit: Int,
        page: Int
    ): OrderListModel {
        return postsRepository.getOrderRequestList(token, type, limit, page)
    }

    suspend fun getOrderInvoice(token: String, orderID: String) =
        postsRepository.getOrderInvoiceAPI(token, orderID)

}
