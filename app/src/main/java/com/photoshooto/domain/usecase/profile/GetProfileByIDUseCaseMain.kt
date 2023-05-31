//package com.photoshooto.domain.usecase.profile
//
//import com.photoshooto.domain.exception.traceErrorException
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import kotlinx.coroutines.*
//import java.util.concurrent.CancellationException
//
//abstract class GetProfileByIDUseCaseMain<Type, in Params>() where Type : Any {
//
//    abstract suspend fun getUserProfileByID(id: String? = null,token: String? = null): Type
//
//    fun getUserProfileByID(
//        scope: CoroutineScope,
//        id: String?, token: String?,
//        onResult: UseCaseResponse<Type>?
//    ) {
//        scope.launch {
//            try {
//                val result = getUserProfileByID(id,token)
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