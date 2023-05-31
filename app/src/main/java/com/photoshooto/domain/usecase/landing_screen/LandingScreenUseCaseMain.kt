package com.photoshooto.domain.usecase.landing_screen

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class LandingScreenUseCaseMain<Type, Type2> where Type : Any, Type2 : Any {

    abstract suspend fun getLandingScreen(token: String, userType: String): Type
    abstract suspend fun getUserLandingScreen(token: String): Type2

    fun getLandingScreen(
        scope: CoroutineScope,
        token: String,
        userType: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getLandingScreen(token, userType)
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

    fun getUserLandingScreen(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type2>?
    ) {
        scope.launch {
            try {
                val result = getUserLandingScreen(token)
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