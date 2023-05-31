package com.photoshooto.domain.usecase.qrCodeSetup.createEvent

import com.photoshooto.domain.model.*
import com.photoshooto.domain.repository.PostsRepository
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateFolderRequest

class CreateEventUseCase constructor(
    private val postsRepository: PostsRepository
) : CreateEventUseCaseMain<GetFolderResponse, GetStandeeResponse, GetQRCodeResponse, GetEventTypesResponse, CommonResponse, CommonResponse>() {

    override suspend fun getFolderList(token: String?): GetFolderResponse {
        return postsRepository.getFolderList(token)
    }

    override suspend fun getStandeeList(token: String?): GetStandeeResponse {
        return postsRepository.getStandeeList(token)
    }

    override suspend fun getQRCodeList(token: String?): GetQRCodeResponse {
        return postsRepository.getQRCodeList(token)
    }

    override suspend fun getEventTypesList(token: String?): GetEventTypesResponse {
        return postsRepository.getEventTypesList(token)
    }

    override suspend fun callCreateFolder(
        token: String?,
        createFolderRequest: CreateFolderRequest
    ): CommonResponse {
        return postsRepository.callCreateFolder(token, createFolderRequest)
    }

    override suspend fun callCreateEvent(
        token: String?,
        createEventRequest: CreateEventRequest
    ): CommonResponse {
        return postsRepository.callCreateEvent(token, createEventRequest)
    }
}