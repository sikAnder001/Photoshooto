//package com.photoshooto.domain.usecase.profile
//
//import com.photoshooto.domain.exception.traceErrorException
//import com.photoshooto.domain.model.UpdateProfileModel
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import kotlinx.coroutines.*
//import okhttp3.RequestBody
//import java.util.concurrent.CancellationException
//
//abstract class UpdateProfileUseCaseMain<Type, in Params>() where Type : Any {
//
//    abstract suspend fun updateUserProfile(body: UpdateProfileModel?=null, token: String? = null): Type
//
//    fun updateUserProfile(
//        scope: CoroutineScope,
//        body: UpdateProfileModel?,
//        token: String?,
//        onResult: UseCaseResponse<Type>?
//    ) {
//        scope.launch {
//            try {
//                val result = updateUserProfile(body,token)
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