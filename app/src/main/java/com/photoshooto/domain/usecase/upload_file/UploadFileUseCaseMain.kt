//package com.photoshooto.domain.usecase.upload_file
//
//import com.photoshooto.domain.exception.traceErrorException
//import com.photoshooto.domain.model.UpdateProfileModel
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import kotlinx.coroutines.*
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import java.util.HashMap
//import java.util.concurrent.CancellationException
//
//abstract class UploadFileUseCaseMain<Type, in Params>() where Type : Any {
//
//    abstract suspend fun updateImgFile(photos: MultipartBody.Part? = null, category: HashMap<String, RequestBody>? =null, token: String? = null): Type
//
//    fun updateImgFile(
//        scope: CoroutineScope,
//        photos: MultipartBody.Part?,
//        category: HashMap<String, RequestBody>?,
//        token: String? = null,
//        onResult: UseCaseResponse<Type>?
//    ) {
//        scope.launch {
//            try {
//                val result = updateImgFile(photos,category, token)
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