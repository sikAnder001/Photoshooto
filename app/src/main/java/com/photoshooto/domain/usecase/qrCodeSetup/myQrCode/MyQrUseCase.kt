package com.photoshooto.domain.usecase.qrCodeSetup.myQrCode

import com.photoshooto.domain.model.GetEventResponse
import com.photoshooto.domain.repository.PostsRepository

class MyQrUseCase constructor(
    private val postsRepository: PostsRepository
) : MyQrUseCaseMain<GetEventResponse>() {

    override suspend fun getEventList(token: String?): GetEventResponse {
        return postsRepository.getEventsList(token)
    }
}