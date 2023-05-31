package com.photoshooto.domain.usecase.product_details

import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.ProductDetailsModel
import com.photoshooto.domain.repository.PostsRepository
import retrofit2.Response

class ProductDetailsUseCase constructor(
    private val postsRepository: PostsRepository
) : ProductDetailsUseCaseMain<ProductDetailsModel, Response<CommonResponse>>() {
    override suspend fun getTshirtsList(token: String, limit: Int, page: Int): ProductDetailsModel {
        return postsRepository.getTshirtList(token, limit, page)
    }

    suspend fun addToCart(token: String, body: AddTshirtToCartRequest): Response<CommonResponse> =
        postsRepository.addTshirtToCart(token, body)

}
