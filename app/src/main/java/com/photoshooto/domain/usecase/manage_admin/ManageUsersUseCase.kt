package com.photoshooto.domain.usecase.manage_admin

import com.photoshooto.domain.model.*
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.repository.PostsRepository

class ManageUsersUseCase constructor(
    private val postsRepository: PostsRepository
) : ManageUsersUseCaseMain<GetUsersResponse, CommonResponse, GetUserDetailsResponse, GetModulesResponse, GetCityResponse, JobResponse>() {
    override suspend fun getUsers(
        token: String,
        limit: Int,
        page: Int,
        role: String
    ): GetUsersResponse {
        return postsRepository.getUsers(token, limit, page, role)
    }

    override suspend fun createUser(token: String, body: AddUserRequest): CommonResponse {
        return postsRepository.createUser(token, body)
    }

    override suspend fun getUserDetails(token: String, id: String): GetUserDetailsResponse {
        return postsRepository.getUserDetails(token, id)
    }

    override suspend fun getModulesList(token: String): GetModulesResponse {
        return postsRepository.getModulesList(token)
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

    override suspend fun removeUser(token: String, id: String): CommonResponse {
        return postsRepository.removeUser(token, id)
    }

    override suspend fun getJobs(token: String): JobResponse {
        return postsRepository.getJobs(token)
    }


    override suspend fun getPhotographerUsers(token: String, role: String): GetUsersResponse {
        return postsRepository.getPhotographerUsers(token, role)
    }
}
