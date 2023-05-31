package com.photoshooto.domain.usecase.payment_success

import com.photoshooto.domain.model.PaymentRequestModel
import com.photoshooto.domain.repository.PostsRepository

class PaymentSuccessUseCase constructor(
    private val postsRepository: PostsRepository
) {

    suspend fun paymentSuccess(token: String, loginBody: PaymentRequestModel) =
        postsRepository.paymentSuccess(token, loginBody)

}
