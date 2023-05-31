package com.photoshooto.domain.usecase.login

import com.photoshooto.domain.model.RegisterVerifyOtpModel
import com.photoshooto.domain.repository.PostsRepository
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel

class UserLoginUseCase constructor(
    private val postsRepository: PostsRepository
) {

    suspend fun userLogin(loginBody: UserLoginRequest) = postsRepository.userLogin(loginBody)
    suspend fun logout(token: String?) = postsRepository.logout(token)
    suspend fun signupVerifyOtpCall(params: RegisterVerifyOtpModel?) =
        postsRepository.signupVerifyOtpCall(params)

    suspend fun sendOtpMobile(params: SendOtpRequestModel) = postsRepository.sendOtpCall(params)

}
