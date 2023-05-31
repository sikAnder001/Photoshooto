//package com.photoshooto.domain.usecase.signup
//
//import com.photoshooto.domain.exception.traceErrorException
//import com.photoshooto.domain.model.RegisterVerifyOtpModel
//import com.photoshooto.domain.model.VerifyMovileModel
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import kotlinx.coroutines.*
//import java.util.concurrent.CancellationException
//
//abstract class SignupUseCaseMain<Type, in Params>() where Type : Any {
//
//    abstract suspend fun signupVerifyOtpCall(params: RegisterVerifyOtpModel? = null): Type
//
//    fun signupVerifyOtpCall(
//        scope: CoroutineScope,
//        params: RegisterVerifyOtpModel?,
//        onResult: UseCaseResponse<Type>?
//    ) {
//        scope.launch {
//            try {
//                val result = signupVerifyOtpCall(params)
//                onResult?.onSuccess(result)
//            } catch (e: CancellationException) {
//                e.printStackTrace()
//                onResult?.onError(traceErrorException(e))
//            } catch (e: Exception) {
//                e.printStackTrace()
//                onResult?.onError(traceErrorException(e))
//            }
//        }
//    }
//
//}