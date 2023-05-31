package com.photoshooto.domain.usecase.product_details

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class ProductDetailsUseCaseMain<Type, Typee> where Type : Any, Typee : Any {

    abstract suspend fun getTshirtsList(token: String, limit: Int, page: Int): Type

    //abstract suspend fun addToCart(token: String, body: AddTshirtToCartRequest): Typee
    fun getTshirtsList(
        scope: CoroutineScope,
        token: String,
        limit: Int,
        page: Int,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getTshirtsList(token, limit, page)
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
    /* fun addToCart(
         scope: CoroutineScope,
         token: String,
         body: AddTshirtToCartRequest,
         onResult: UseCaseResponse<Typee>?
     ) {
         scope.launch {
             try {
                 val result = addToCart(token, body)
                 onResult?.onSuccess(result)
             } catch (e: CancellationException) {
                 e.printStackTrace()
                 onResult?.onError(traceErrorException(e))
             } catch (e: Exception) {
                 e.printStackTrace()
                 onResult?.onError(traceErrorException(e))
             }
         }
     }*/
}
