package com.photoshooto.domain.usecase.qr_generation

import com.photoshooto.domain.model.GenerateQrCodeResponse
import com.photoshooto.domain.model.GetStandeeResponse
import com.photoshooto.domain.repository.PostsRepository

class QrGenerationUseCase constructor(
    private val postsRepository: PostsRepository
) : QrGenerationUseCaseMain<GetStandeeResponse, GenerateQrCodeResponse>() {
    override suspend fun getStandee(token: String): GetStandeeResponse {
        return postsRepository.getStandee(token)
    }

    override suspend fun generateQrCode(token: String): GenerateQrCodeResponse {
        return postsRepository.generateQrCode(token)
    }
}
