package com.photoshooto.domain.usecase.verify_otp

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class VerifyOtpUseCaseMain<Type, in Params>() where Type : Any {

    abstract suspend fun verifyOtp(verifyKey: VerifyOTPRequestModel? = null): Type

    fun verifyOtp(
        scope: CoroutineScope,
        verifyKey: VerifyOTPRequestModel? = null,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = verifyOtp(verifyKey)
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