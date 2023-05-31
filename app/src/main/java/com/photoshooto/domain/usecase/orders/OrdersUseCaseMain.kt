package com.photoshooto.domain.usecase.orders

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class OrdersUseCaseMain<Type> where Type : Any {

    abstract suspend fun getOrderRequestList(
        token: String,
        type: String,
        limit: Int,
        page: Int
    ): Type

    fun getOrderRequestList(
        scope: CoroutineScope,
        token: String,
        type: String,
        limit: Int,
        page: Int,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getOrderRequestList(token, type, limit, page)
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
