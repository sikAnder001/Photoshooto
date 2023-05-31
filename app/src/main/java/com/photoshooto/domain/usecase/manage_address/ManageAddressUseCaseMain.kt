package com.photoshooto.domain.usecase.manage_address

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class ManageAddressUseCaseMain<Type, Type2, Type3> where Type : Any, Type2 : Any, Type3 : Any {

    abstract suspend fun getAddress(token: String): Type
    abstract suspend fun addAddress(token: String, body: AddAddressRequest): Type2
    abstract suspend fun updateAddress(token: String, id: String, body: AddAddressRequest): Type2
    abstract suspend fun deleteAddress(token: String, id: String): Type2
    abstract suspend fun deletePlan(token: String, id: String): Type2
    abstract suspend fun getStates(token: String): Type3
    fun getAddress(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getAddress(token)
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

    fun addAddress(
        scope: CoroutineScope,
        token: String,
        body: AddAddressRequest,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = addAddress(token, body)
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

    fun updateAddress(
        scope: CoroutineScope,
        token: String,
        id: String,
        body: AddAddressRequest,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = updateAddress(token, id, body)
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

    fun deleteAddress(
        scope: CoroutineScope,
        token: String,
        id: String,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = deleteAddress(token, id)
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

    fun deletePlan(
        scope: CoroutineScope,
        token: String,
        id: String,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = deletePlan(token, id)
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

    fun getStates(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type3>?
    ) {
        scope.launch {
            try {
                val result = getStates(token)
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
