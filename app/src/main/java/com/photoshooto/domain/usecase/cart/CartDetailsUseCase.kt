package com.photoshooto.domain.usecase.cart

import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.CreateOrderModel
import com.photoshooto.domain.model.GetAddressResponse
import com.photoshooto.domain.model.GetCartDetailsResponse
import com.photoshooto.domain.repository.PostsRepository
import com.photoshooto.domain.usecase.qr_generation.CreateStandeeOrderRequest
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper

class CartDetailsUseCase constructor(
    private val postsRepository: PostsRepository
) : CartDetailsUseCaseMain<CreateOrderModel, CreateOrderModel, GetCartDetailsResponse, GetAddressResponse, CommonResponse>() {
    override suspend fun createTshirtOrder(
        token: String,
        body: CreateTshirtOrderRequest
    ): CreateOrderModel {
        return postsRepository.createTshirtOrder(token, body)
    }

    override suspend fun getCartDetails(token: String): GetCartDetailsResponse {
        return postsRepository.getCartDetails(token)
    }

    override suspend fun getUsersAddress(token: String): GetAddressResponse {
        return postsRepository.getUsersAddress(token)
    }

    override suspend fun createStandeeOrder(
        token: String,
        body: CreateStandeeOrderRequest
    ): CreateOrderModel {
        return postsRepository.createStandeeOrder(token, body)
    }

    override suspend fun removeCartDetails(): CommonResponse {
        return postsRepository.removeCartDetails(
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString()
        )
    }
}
