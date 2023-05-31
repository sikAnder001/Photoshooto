package com.photoshooto.domain.usecase.qr_event_order

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class EventOrderHistoryDetailsUseCaseMain<Type, Typee>() where Type : Any, Typee : Any {

    abstract suspend fun getEventOrderHistory(token: String, limit: Int, page: Int): Type

    fun getEventOrderHistory(
        scope: CoroutineScope,
        token: String,
        limit: Int,
        page: Int,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getEventOrderHistory(token, limit, page)
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