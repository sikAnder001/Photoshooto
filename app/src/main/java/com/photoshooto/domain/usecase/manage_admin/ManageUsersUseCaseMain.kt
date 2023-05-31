package com.photoshooto.domain.usecase.manage_admin

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.model.UpdateUserStatusRequest
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class ManageUsersUseCaseMain<Type, Type2, Type3, Type4, Type5, T> where Type : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any {

    abstract suspend fun getUsers(token: String, limit: Int, page: Int, role: String): Type
    abstract suspend fun getPhotographerUsers(token: String, role: String): Type
    abstract suspend fun getUserDetails(token: String, id: String): Type3
    abstract suspend fun getModulesList(token: String): Type4
    abstract suspend fun getCityList(token: String): Type5
    abstract suspend fun createUser(token: String, body: AddUserRequest): Type2
    abstract suspend fun removeUser(token: String, id: String): Type2

    abstract suspend fun getJobs(token: String): T

    abstract suspend fun updateUserStatus(
        token: String,
        id: String,
        body: UpdateUserStatusRequest
    ): Type2

    fun getModulesList(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type4>?
    ) {
        scope.launch {
            try {
                val result = getModulesList(token)
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
        onResult: UseCaseResponse<Type5>?
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

    fun getPhotographerUsers(
        scope: CoroutineScope,
        token: String,
        type: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getPhotographerUsers(token, type)
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
        onResult: UseCaseResponse<Type3>?
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

    fun createUser(
        scope: CoroutineScope,
        token: String,
        body: AddUserRequest,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = createUser(token, body)
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

    fun removeUser(
        scope: CoroutineScope,
        token: String,
        id: String,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = removeUser(token, id)
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
        onResult: UseCaseResponse<Type2>?
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


    fun getJobs(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<T>?
    ) {
        scope.launch {
            try {
                val result = getJobs(token)
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
