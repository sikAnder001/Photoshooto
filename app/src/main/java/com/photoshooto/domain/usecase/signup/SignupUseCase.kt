//package com.photoshooto.domain.usecase.signup
//
//import com.photoshooto.domain.model.RegisterVerifyOtpModel
//import com.photoshooto.domain.model.VerifyMovileModel
//import com.photoshooto.domain.repository.PostsRepository
//
//class SignupUseCase constructor(
//    private val postsRepository: PostsRepository
//) : SignupUseCaseMain<VerifyMovileModel,  RegisterVerifyOtpModel?>() {
//
//    override suspend fun signupVerifyOtpCall(params:  RegisterVerifyOtpModel?): VerifyMovileModel {
//        return postsRepository.signupVerifyOtpCall(params)
//    }
//
//}