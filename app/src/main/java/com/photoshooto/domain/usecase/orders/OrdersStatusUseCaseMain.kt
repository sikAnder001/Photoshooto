package com.photoshooto.domain.usecase.orders

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.model.UpdateStatus
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class OrdersStatusUseCaseMain<Type>() where Type : Any {


    abstract suspend fun updateOrderStatus(
        token: String,
        status: UpdateStatus,
        order_id: String
    ): Type

    fun updateOrderStatus(
        scope: CoroutineScope,
        token: String,
        status: UpdateStatus,
        order_id: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = updateOrderStatus(token, status, order_id)
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
