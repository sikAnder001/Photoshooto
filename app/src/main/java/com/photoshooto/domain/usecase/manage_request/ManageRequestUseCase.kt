package com.photoshooto.domain.usecase.manage_request

import com.photoshooto.domain.model.*
import com.photoshooto.domain.repository.PostsRepository

class ManageRequestUseCase constructor(
    private val postsRepository: PostsRepository
) : ManageRequestUseCaseMain<GetUsersResponse, GetUserDetailsResponse, GetReasonsResponse, GetCityResponse, CommonResponse>() {
    override suspend fun getUsers(
        token: String,
        limit: Int,
        page: Int,
        role: String
    ): GetUsersResponse {
        return postsRepository.getUsers(token, limit, page, role)
    }

    override suspend fun getUserDetails(token: String, id: String): GetUserDetailsResponse {
        return postsRepository.getUserDetails(token, id)
    }

    override suspend fun getReasons(token: String): GetReasonsResponse {
        return postsRepository.getReasonsList(token)
    }

    override suspend fun getUsers(
        token: String,
        limit: Int,
        page: Int,
        query: String?,
        sortBy: String?,
        order: String?,
        cityAssigned: String?,
        status: String
    ): GetUsersResponse {
        return postsRepository.getUsers(
            token,
            limit,
            page,
            query,
            sortBy,
            order,
            cityAssigned,
            status
        )
    }

    override suspend fun getCityList(token: String): GetCityResponse {
        return postsRepository.getTierCityList(token)
    }

    override suspend fun updateUserStatus(
        token: String,
        id: String,
        body: UpdateUserStatusRequest
    ): CommonResponse {
        return postsRepository.updateUserStatus(token, id, body)
    }
}
