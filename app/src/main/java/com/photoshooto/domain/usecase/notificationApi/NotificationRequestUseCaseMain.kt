package com.photoshooto.domain.usecase.notificationApi

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class NotificationRequestUseCaseMain<Type, in Params>() where Type : Any {

    abstract suspend fun reqNotifications(params: Params? = null, token: String? = null): Type
    fun reqNotificationsCall(
        scope: CoroutineScope,
        params: Params?,
        token: String?,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = reqNotifications(params, token)
                onResult?.onSuccess(result)
            } catch (e: CancellationException) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
                println("praveen traceErrorException(e) " + traceErrorException(e))
            } catch (e: Exception) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
                println("praveen traceErrorException(e) " + traceErrorException(e))
            }
        }
    }

}