package com.photoshooto.domain.usecase.cart

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.qr_generation.CreateStandeeOrderRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class CartDetailsUseCaseMain<Type, Standee, Cart, Address, Common> where Type : Any, Standee : Any, Cart : Any, Address : Any, Common : Any {

    abstract suspend fun createTshirtOrder(token: String, body: CreateTshirtOrderRequest): Type
    abstract suspend fun createStandeeOrder(token: String, body: CreateStandeeOrderRequest): Standee
    abstract suspend fun getCartDetails(token: String): Cart
    abstract suspend fun getUsersAddress(token: String): Address
    abstract suspend fun removeCartDetails(): Common


    fun createTshirtOrder(
        scope: CoroutineScope,
        token: String,
        body: CreateTshirtOrderRequest,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = createTshirtOrder(token, body)
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

    fun createStandeeOrder(
        scope: CoroutineScope,

        token: String,
        body: CreateStandeeOrderRequest,
        onResult: UseCaseResponse<Standee>?
    ) {
        scope.launch {
            try {
                val result = createStandeeOrder(token, body)
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

    fun getCartDetails(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Cart>?
    ) {
        scope.launch {
            try {
                val result = getCartDetails(token)
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

    fun getUsersAddress(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Address>?
    ) {
        scope.launch {
            try {
                val result = getUsersAddress(token)
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

    fun removeCartDetails(
        scope: CoroutineScope,
        onResult: UseCaseResponse<Common>?
    ) {
        scope.launch {
            try {
                val result = removeCartDetails()
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
