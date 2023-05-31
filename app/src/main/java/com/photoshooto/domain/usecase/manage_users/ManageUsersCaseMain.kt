package com.photoshooto.domain.usecase.manage_users

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class ManageUsersCaseMain<Type, Type2>() where Type : Any, Type2 : Any {

    abstract suspend fun getUsers(token: String, limit: Int, page: Int, role: String): Type
    abstract suspend fun getUserDetails(token: String, id: String): Type2
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
}
