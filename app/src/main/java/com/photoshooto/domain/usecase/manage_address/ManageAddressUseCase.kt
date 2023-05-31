package com.photoshooto.domain.usecase.manage_address

import com.photoshooto.domain.model.CommonResponse
import com.photoshooto.domain.model.GetAddressResponse
import com.photoshooto.domain.model.GetStatesResponse
import com.photoshooto.domain.repository.PostsRepository

class ManageAddressUseCase constructor(
    private val postsRepository: PostsRepository
) : ManageAddressUseCaseMain<GetAddressResponse, CommonResponse, GetStatesResponse>() {
    override suspend fun getAddress(token: String): GetAddressResponse {
        return postsRepository.getUsersAddress(token)
    }

    override suspend fun addAddress(token: String, body: AddAddressRequest): CommonResponse {
        return postsRepository.addAddress(token, body)
    }

    override suspend fun updateAddress(
        token: String,
        id: String,
        body: AddAddressRequest
    ): CommonResponse {
        return postsRepository.updateAddress(token, id, body)
    }

    override suspend fun getStates(token: String): GetStatesResponse {
        return postsRepository.getStatesList(token)
    }

    override suspend fun deleteAddress(token: String, id: String): CommonResponse {
        return postsRepository.deleteUserAddress(token, id)
    }

    override suspend fun deletePlan(id: String, token: String): CommonResponse {
        return postsRepository.deletePlan(id, token)
    }
}