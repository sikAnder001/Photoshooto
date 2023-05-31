//package com.photoshooto.domain.usecase.logout
//
//import com.photoshooto.domain.exception.traceErrorException
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import com.photoshooto.domain.usecase.service.ServiceValidRequest
//import kotlinx.coroutines.*
//import okhttp3.RequestBody
//import java.util.HashMap
//import java.util.concurrent.CancellationException
//
//abstract class LogoutUseCaseMain<Type, in Params>() where Type : Any {
//
//    abstract suspend fun logout(token: String? = null): Type
//    fun logout(
//        scope: CoroutineScope,
//        token: String? = null,
//        onResult: UseCaseResponse<Type>?
//    ) {
//        scope.launch {
//            try {
//                val result = logout(token)
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