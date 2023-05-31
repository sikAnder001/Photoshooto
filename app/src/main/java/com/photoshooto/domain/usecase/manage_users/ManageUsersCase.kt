package com.photoshooto.domain.usecase.manage_users

import com.photoshooto.domain.model.GetUserDetailsResponse
import com.photoshooto.domain.model.GetUsersResponse
import com.photoshooto.domain.repository.PostsRepository

class ManageUsersCase constructor(
    private val postsRepository: PostsRepository
) : ManageUsersCaseMain<GetUsersResponse, GetUserDetailsResponse>() {
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
}
