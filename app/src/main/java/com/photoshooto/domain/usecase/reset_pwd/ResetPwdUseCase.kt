package com.photoshooto.domain.usecase.reset_pwd

import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.repository.PostsRepository

class ResetPwdUseCase constructor(
    private val postsRepository: PostsRepository
) : ResetPwdUseCaseMain<AllStatusModel, Any?>() {
    override suspend fun resetpassword(
        body: PwdChangeRequestModel?, token: String?
    ): AllStatusModel {
        return postsRepository.resetpassword(body, token)
    }
}
