//package com.photoshooto.domain.usecase.profile
//
//import com.photoshooto.domain.exception.traceErrorException
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import kotlinx.coroutines.*
//import java.util.concurrent.CancellationException
//
//abstract class GetUserProfileUseCaseMain<Type, in Params>() where Type : Any {
//
//    abstract suspend fun getUserProfileData(token: String? = null): Type
//
//    fun getUserProfileData(
//        scope: CoroutineScope,
//        token: String?,
//        onResult: UseCaseResponse<Type>?
//    ) {
//        scope.launch {
//            try {
//                val result = getUserProfileData(token)
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