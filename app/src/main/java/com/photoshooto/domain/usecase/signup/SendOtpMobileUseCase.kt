//package com.photoshooto.domain.usecase.signup
//
//import com.photoshooto.domain.model.VerifyMovileModel
//import com.photoshooto.domain.repository.PostsRepository
//import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
//
//class SendOtpMobileUseCase constructor(
//    private val postsRepository: PostsRepository
//) : SendOtpMobileUseCaseMain<VerifyMovileModel, Any?>() {
//
//    override suspend fun sendOtpCall(params: SendOtpRequestModel?): VerifyMovileModel {
//        return postsRepository.sendOtpCall(params)
//    }
//
//}