package com.photoshooto.domain.usecase.verify_otp

import com.photoshooto.domain.model.UserProfileModel
import com.photoshooto.domain.repository.PostsRepository

class VerifyOtpUseCase constructor(
    private val postsRepository: PostsRepository
) : VerifyOtpUseCaseMain<UserProfileModel, Any?>() {
    override suspend fun verifyOtp(
        body: VerifyOTPRequestModel?
    ): UserProfileModel {
        return postsRepository.verifyOtp(body)
    }
}
