package com.photoshooto.ui.notification

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class GetNotificationUseCaseMain<Type, in Params>() where Type : Any {

    abstract suspend fun getNotificationData(token: String? = null): Type

    fun getNotificationData(
        scope: CoroutineScope,
        token: String?,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getNotificationData(token)
                onResult?.onSuccess(result)
            } catch (e: CancellationException) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
            } catch (e: Exception) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
            }
        }
    }

}