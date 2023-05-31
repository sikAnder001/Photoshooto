package com.photoshooto.domain.usecase.manage_request

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.model.UpdateUserStatusRequest
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class ManageRequestUseCaseMain<Type, Type2, Type3, Type4, Type5>() where Type : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any {

    abstract suspend fun getUsers(token: String, limit: Int, page: Int, role: String): Type
    abstract suspend fun getCityList(token: String): Type4
    abstract suspend fun getUsers(
        token: String,
        limit: Int,
        page: Int,
        query: String? = null,
        sortBy: String? = null,
        order: String? = null,
        cityAssigned: String? = null,
        status: String
    ): Type

    abstract suspend fun updateUserStatus(
        token: String,
        id: String,
        body: UpdateUserStatusRequest
    ): Type5

    abstract suspend fun getUserDetails(token: String, id: String): Type2
    abstract suspend fun getReasons(token: String): Type3
    fun getReasons(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type3>?
    ) {
        scope.launch {
            try {
                val result = getReasons(token)
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

    fun getUsers(
        scope: CoroutineScope,
        token: String,
        limit: Int,
        page: Int,
        role: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getUsers(token, limit, page, role)
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

    fun getCityList(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type4>?
    ) {
        scope.launch {
            try {
                val result = getCityList(token)
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

    fun getUsers(
        scope: CoroutineScope,
        token: String,
        limit: Int,
        page: Int,
        query: String? = null,
        sortBy: String? = null,
        order: String? = null,
        cityAssigned: String? = null,
        status: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result =
                    getUsers(token, limit, page, query, sortBy, order, cityAssigned, status)
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

    fun getUserDetails(
        scope: CoroutineScope,
        token: String,
        id: String,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = getUserDetails(token, id)
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

    fun updateUserStatus(
        scope: CoroutineScope,
        token: String,
        id: String,
        body: UpdateUserStatusRequest,
        onResult: UseCaseResponse<Type5>?
    ) {
        scope.launch {
            try {
                val result = updateUserStatus(token, id, body)
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
